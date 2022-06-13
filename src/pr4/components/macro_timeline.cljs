(ns pr4.components.macro-timeline
  (:require
    [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
    [google-charts]
    [pr4.app-state :as app-state]
    [pr4.events :as events]
    [pr4.periodization :as periodization]
    [pr4.util :as util]
    [re-frame.core :as re-frame]
    [reagent.core :as reagent]
    [tick.alpha.api :as tick]))

(defn redefine-periodization-week-sessions
  [app-state]
  (get-in app-state [:domain :redefine-periodization :week-sessions]))
(re-frame/reg-sub
  ::redefine-periodization-week-sessions
  redefine-periodization-week-sessions)

(defn redefine-periodization-planning-start
  [app-state]
  (get-in app-state [:domain :redefine-periodization :planning-start]))
(re-frame/reg-sub
  ::redefine-periodization-planning-start
  redefine-periodization-planning-start)

(defn redefine-periodization-planning-end
  [app-state]
  (get-in app-state [:domain :redefine-periodization :planning-end]))
(re-frame/reg-sub
  ::redefine-periodization-planning-end
  redefine-periodization-planning-end)

(defn get-in-redefine-periodization [app-state]
  (let [week-sessions (redefine-periodization-week-sessions app-state)
        days-of-training (mapv {"Segunda" tick/MONDAY "Terça" tick/TUESDAY "Quarta" tick/WEDNESDAY
                                "Quinta" tick/THURSDAY "Sexta" tick/FRIDAY "Sábado" tick/SATURDAY
                                "Domingo" tick/SUNDAY} week-sessions)
        start-date (redefine-periodization-planning-start app-state)
        end-date (redefine-periodization-planning-end app-state)
        sessions-time-target (get-in app-state [:domain :redefine-periodization :sessions-time-target])]
    [days-of-training start-date end-date sessions-time-target]))

;; No futuro isso provavelmente vai precisar ser calculado a partir dos
;; dados domain.
(defn macrocycle-timeline [days-of-training initial-date end-date]
  (let [total-days (util/days-difference (util/plus-days end-date -3) initial-date)
        days-per-micro (/ 28 (count days-of-training))
        micros-amount (Math.floor (/ total-days days-per-micro))
        phases (remove zero? (periodization/count-phases micros-amount))
        mesos (mapcat #(periodization/phase-mesos %2 %1) ["Básica" "Específica" "Transição"] phases)
        micros (mapcat #(apply periodization/meso-micros %) (map vals mesos))
        offset-list #(->> %1 cycle (drop %2) (take (count %1)))
        final-micro-date
          (fn [start]
            (loop [i 4
                   date (util/plus-days start 1)
                   week-days-of-training (loop [current (util/plus-days start 1)
                                                index (.indexOf days-of-training (tick/day-of-week (tick/date current)))]
                                           (if (= -1 index)
                                             (recur (util/plus-days current 1)
                                                    (.indexOf days-of-training (tick/day-of-week (tick/date (util/plus-days current 1)))))
                                             (offset-list days-of-training index)))]
              (cond
                (= i 0) (util/plus-days date -1)
                (= (first week-days-of-training)
                   (tick/day-of-week (tick/date date)))
                (recur (dec i)
                       (util/plus-days date 1)
                       (offset-list week-days-of-training 1))
                :else (recur i (util/plus-days date 1) week-days-of-training))))
        micros->dates
          (fn [val [type category micros-amount]]
            (let [last-date (or (last (last val)) (util/plus-days initial-date -1))
                  final (-> (iterate final-micro-date last-date) (nth micros-amount))
                  start (util/plus-days last-date 1)]
              (conj val [type category (str start) (str final)])))
        macro-final-date (-> (iterate final-micro-date (util/plus-days initial-date -1)) (nth micros-amount))]
    (concat (reduce micros->dates [] (map #(into ["Micro" % 1]) micros))
            (reduce micros->dates [] (map #(into ["Meso" (:category %) (:micros-amount %)]) mesos))
            (reduce micros->dates [] (map #(into ["Fase" %1 %2])
                                          ["Básica" "Específica" "Transição"]
                                          phases))
            [["Macro" "Macrocíclo" initial-date (str macro-final-date)]])))

(defn-traced update-timeline
  [app-state]
  (let [timeline (apply macrocycle-timeline (get-in-redefine-periodization app-state))
        [_ _ start end] (last timeline)]
    (-> app-state
        (assoc-in [:ui :macrocycle-timeline] timeline)
        (assoc-in [:ui :zoom-dates] [start end]))))
(re-frame/reg-event-db ::update-timeline update-timeline)

(defn draw-timeline-chart [el]
  (fn []
    (let [g-charts-instance (new google-charts)
          data-array-input @(re-frame/subscribe
                              [::app-state/macrocycle-timeline])
          color-map {"Incorporação"  "#8869ad"
                     "Incorporativo" "#8869ad"
                     "Ordinário"     "#86acd3"
                     "Base"          "#86acd3"
                     "Estabilizador" "#b7a6cd"
                     "Choque 1"      "#b7a6cd"
                     "Choque 2"      "#3276b5"
                     "Controle"      "#4cb4b9"
                     "Auge"          "#3276b5"
                     "Recuperação"   "#83d1d5"
                     "Recuperativo"  "#83d1d5"
                     "Básica"        "#69419b"
                     "Específica"    "#2965af"
                     "Transição"     "#44a3a8"
                     "Macrocíclo"    "#3bbab5"}
          data-array (->> data-array-input
                          (map (fn [[type name start end]]
                                 [type name (color-map name) (tick/inst (str start "T00:00")) (tick/inst (str end "T00:00"))]))
                          (cons ["Type" "Name" #js {:role "style"} "Start" "End"])
                          (map clj->js)
                          clj->js)
          data #(-> g-charts-instance .-api .-visualization
                    (.arrayToDataTable data-array))
          options #js {:timeline #js {:showRowLabels false}}
          draw-fn (fn []
                    (let [chart (new (-> g-charts-instance .-api
                                         .-visualization .-Timeline) el)
                          select-handler (fn []
                                           (-> chart
                                               (.getSelection)
                                               first
                                               (.-row)
                                               (#(nth data-array-input % (last data-array-input)))
                                               (#(re-frame/dispatch [::events/zoom-macro-timeline %]))))]
                      (-> g-charts-instance
                          (.-api)
                          (.-visualization)
                          (.-events)
                          (.addListener chart "select" select-handler))
                      (-> chart (.draw (data) options))))]
      (-> g-charts-instance (.load draw-fn)))))

(defn macro-timeline [{:keys [style]}]
  [(util/with-mount-fn
     [:div
      {:style style
       :data-hack-to-update-element
         (first @(re-frame/subscribe
                   [::app-state/macrocycle-timeline]))
       :component-did-mount
         (fn [element-ref]
           (-> (new google-charts)
               (.load "current"
                      #js {"packages" #js ["timeline"]
                           ;; "language" "pt"
                           "callback" (draw-timeline-chart
                                        (reagent/dom-node element-ref))})))}
      "Carregando.."])])
