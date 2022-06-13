(ns pr4.components.sessions-chart
  (:require
    [google-charts]
    [pr4.app-state :as app-state]
    [pr4.periodization :as periodization]
    [pr4.util :as util]
    [re-frame.core :as re-frame]
    [reagent.core :as reagent]
    [tick.alpha.api :as tick]))

(defn sessions-with-dates-idx
  [app-state]
  (let [first-day (get-in app-state [:domain :calendar :first-day])
        days (get-in app-state [:domain :calendar :days])
        sessions (get-in app-state [:domain :sessions])
        idx-date (map #(into [(:session-idx %1) %2])
                      days
                      (iterate #(util/plus-days % 1) first-day))]
    (->> idx-date
         (remove #(-> % first nil?))
         (map (fn [[idx date]]
                (assoc
                  (nth sessions idx
                       {:reps-min 20 :reps-max 20 :total-sets 1 :rest-time 10})
                  :date date
                  :idx idx))))))
(re-frame/reg-sub ::sessions-with-dates-idx sessions-with-dates-idx)

(defn zoom-dates [app-state]
  (get-in app-state [:ui :zoom-dates]))
(re-frame/reg-sub ::zoom-dates zoom-dates)

(defn draw-chart [data-array options]
  (fn [element-ref]
    (let [g-charts-instance (new google-charts)
          el (reagent/dom-node element-ref)
          data (fn [] (-> g-charts-instance .-api .-visualization
                          (.arrayToDataTable data-array)))
          chart #(new (-> g-charts-instance
                          .-api
                          .-visualization
                          .-ColumnChart)
                      el)
          draw-fn (fn [] (-> (chart) (.draw (data) options)))]
      (-> g-charts-instance (.load draw-fn)))))

(defn reps->intensity-text [min max]
  (if (= min max)
    (str (Math.round (periodization/reps-to-intensity min)) "%")
    (str (Math.round (periodization/reps-to-intensity max)) "%-"
         (Math.round (periodization/reps-to-intensity min)) "%")))

(defn date-str [date]
  (let [dd (tick/day-of-month date)
        MM (tick/int (tick/month date))
        yyyy (tick/int (tick/year date))
        day (tick/day-of-week date)
        pt-week {tick/MONDAY "Segunda"
                 tick/TUESDAY "Terça"
                 tick/WEDNESDAY "Quarta"
                 tick/THURSDAY "Quinta"
                 tick/FRIDAY "Sexta"
                 tick/SATURDAY "Sábado"
                 tick/SUNDAY "Domingo"}]
    (str dd "/" MM "/" yyyy
         " (" (clojure.string/join (take 3 (pt-week day))) ")")))

(defn sessions->chart-data [_ _]
  (let [sessions-with-dates-idx @(re-frame/subscribe [::sessions-with-dates-idx])
        date-between? #(tick/<= (if %2 (tick/date %2) (tick/date %1))
                                (tick/date %1)
                                (if %3 (tick/date %3) (tick/date %1)))
        zoom-dates @(re-frame/subscribe [::zoom-dates])
        row-fn (fn [{:keys [idx date total-sets reps-max reps-min rest-time]}]
                 (let [vol-min (periodization/session-volume reps-min total-sets)
                       vol-max (periodization/session-volume reps-max total-sets)
                       int-min (periodization/reps-to-intensity reps-max)
                       int-max (periodization/reps-to-intensity reps-min)]
                   #js [(str "Sessão " (inc idx))
                      vol-min
                      "color: #3bbcb7"
                      (str "Sessão " (inc idx)
                           "\n" (date-str date)
                           "\n\n" total-sets " séries"
                           "\n" reps-min "-" reps-max " repetições")
                      vol-min
                      vol-max
                      int-min
                      "color: #ffd237"
                      (str "Sessão " (inc idx)
                           "\n" (date-str date)
                           "\n\nIntensidade: "
                           (reps->intensity-text
                             reps-min
                             reps-max) " de RM"
                           "\nRecuperação: " (util/seconds->minute-text rest-time))
                      int-min
                      int-max]))]
    (->> sessions-with-dates-idx
         (filter #(apply date-between? (:date %) zoom-dates))
         (map row-fn)
         (cons #js ["Sessão"
                    "Volume"
                    #js {:role "style"}
                    #js {:role "tooltip"}
                    #js {:role "interval"}
                    #js {:role "interval"}
                    "Intensidade"
                    #js {:role "style"}
                    #js {:role "tooltip"}
                    #js {:role "interval"}
                    #js {:role "interval"}])
         into-array)))

(def normal-chart-options
  #js {:theme "maximized"
       :legend #js {:position "none"}
       :series #js {"0" #js {:targetAxisIndex 0}
                    "1" #js {:targetAxisIndex 1}}
       :bar #js {:groupWidth "70%"}
       :vAxis #js {:gridlines #js {:color "transparent"}}
       :vAxes #js [#js {:textPosition "none"
                        :viewWindowMode "maximized"}
                   #js {:textPosition "none"
                        :viewWindowMode "maximized"}]})

(defn sessions-chart [{:keys [style]}]
  [(util/with-mount-fn
     [:div
      {:style style
       :component-did-mount
       (draw-chart (sessions->chart-data @(re-frame/subscribe
                                             [::app-state/sessions])
                                          @(re-frame/subscribe [::zoom-dates]))
                   normal-chart-options)}
      "Carregando.."])])
