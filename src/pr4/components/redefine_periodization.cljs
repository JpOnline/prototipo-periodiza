(ns pr4.components.redefine-periodization
  (:require
    [re-frame.core :as re-frame]
    [button :as material-Button]
    [reagent.core :as reagent]
    [pr4.util :as util]
    [pr4.periodization :as periodization]
    [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
    [tick.alpha.api :as tick]
    )
  )

(defn redef-prdz-state
  [app-state]
  (get-in app-state [:domain :redefine-periodization :state]))
(re-frame/reg-sub
  ::redef-prdz-state
  redef-prdz-state)

(defn-traced redef-prdz-box-clicked
  [app-state [_ redef-prdz-state]]
  (-> app-state
      (assoc-in [:domain :redefine-periodization :state] redef-prdz-state)
      (assoc-in [:domain :backup] (:domain app-state))))
(re-frame/reg-event-db ::redef-prdz-box-clicked redef-prdz-box-clicked)

(defn redef-prdz-box-display
  [state]
  (case state
    "editing-goal-level" "none"
    "editing-week" "none"
    "editing-time" "none"
    "editing-duration" "none"
    "flex"))
(re-frame/reg-sub
  ::redef-prdz-box-display
  :<- [::redef-prdz-state]
  redef-prdz-box-display)

(defn redef-prdz-level-goal-display
  [state]
  (case state
    "editing-goal-level" {:display "flex"}
    {:display "none"}))
(re-frame/reg-sub
  ::redef-prdz-level-goal-display
  :<- [::redef-prdz-state]
  redef-prdz-level-goal-display)

(defn redef-prdz-week-display
  [state]
  (case state
    "editing-week" {:display "flex"}
    {:display "none"}))
(re-frame/reg-sub
  ::redef-prdz-week-display
  :<- [::redef-prdz-state]
  redef-prdz-week-display)

(defn redef-prdz-time-display
  [state]
  (case state
    "editing-time" "grid"
    "none"))
(re-frame/reg-sub
  ::redef-prdz-time-display
  :<- [::redef-prdz-state]
  redef-prdz-time-display)

(defn redef-prdz-duration-display
  [state]
  (case state
    "editing-duration" "flex"
    "none"))
(re-frame/reg-sub
  ::redef-prdz-duration-display
  :<- [::redef-prdz-state]
  redef-prdz-duration-display)

(defn-traced redef-prdz-edit-cancel
  [app-state]
  (assoc-in app-state [:domain]
            (get-in app-state [:domain :backup])))
(re-frame/reg-event-db ::redef-prdz-edit-cancel redef-prdz-edit-cancel)

(defn-traced redef-prdz-edit-ok
  [app-state]
  (-> app-state
      (update-in [:domain :redefine-periodization] dissoc :state)
      (update-in [:domain] dissoc :backup)))
(re-frame/reg-event-db ::redef-prdz-edit-ok redef-prdz-edit-ok)

(defn redef-prdz-buttons-display
  [state]
  (case state
    "editing-goal-level" {:display "flex"}
    "editing-week" {:display "flex"}
    "editing-time" {:display "flex"}
    "editing-duration" {:display "flex"}
    {:display "none"}))
(re-frame/reg-sub
  ::redef-prdz-buttons-display
  :<- [::redef-prdz-state]
  redef-prdz-buttons-display)

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

(defn valid-redef-args?
  [app-state]
  (let [[days-of-training initial-date end-date sessions-time-target]
          (get-in-redefine-periodization app-state)
        total-days (util/days-difference end-date initial-date)
        days-per-micro (/ 28 (count days-of-training))
        micros-amount (Math.floor (/ total-days days-per-micro))]
    (cond
      (= 0 (count days-of-training))
        (do (js/alert "Selecione pelo menos um dia na semana de treino.")
            false)
      (< micros-amount 7)
        (do (js/alert "Desculpe, mas com essa alteração o planejamento fica muito curto.")
            false)
      (= 7 (count days-of-training))
        (do (js/alert "Não é bom ficar pelo menos um dia na semana sem treinar? É só uma sugestão ;)")
            true)
      (<= sessions-time-target 0)
        (do (js/alert "O tempo médio por sessão tem que ser maior que zero :/")
            false)
      :else true)))

(defn redefine-periodization-goal-txt
  [app-state]
  (get-in app-state [:domain :redefine-periodization :goal]))
(re-frame/reg-sub
  ::redefine-periodization-goal-txt
  redefine-periodization-goal-txt)

(defn redefine-periodization-goal
  [goal]
  (assoc {"Hipertrofia"  "deselected"
          "Força"        "deselected"
          "Potência"     "deselected"
          "Resistência"  "deselected"}
         goal
         "selected-light"))
(re-frame/reg-sub
  ::redefine-periodization-goal
  :<- [::redefine-periodization-goal-txt]
  redefine-periodization-goal)

(defn-traced redefine-periodization-goal-select
  [app-state [event new-goal]]
  (assoc-in app-state [:domain :redefine-periodization :goal] new-goal))
(re-frame/reg-event-db
  ::redefine-periodization-goal-select
  redefine-periodization-goal-select)

(defn redefine-periodization-level-txt
  [app-state]
  (get-in app-state [:domain :redefine-periodization :level]))
(re-frame/reg-sub
  ::redefine-periodization-level-txt
  redefine-periodization-level-txt)

(defn redefine-periodization-level
  [level]
  (assoc {"Iniciante"     "deselected"
          "Intermediário" "deselected"
          "Avançado"      "deselected"}
         level
         "selected-light"))
(re-frame/reg-sub
  ::redefine-periodization-level
  :<- [::redefine-periodization-level-txt]
  redefine-periodization-level)

(defn-traced redefine-periodization-level-select
  [app-state [event new-level]]
  (assoc-in app-state [:domain :redefine-periodization :level] new-level))
(re-frame/reg-event-db
  ::redefine-periodization-level-select
  redefine-periodization-level-select)

(defn redefine-periodization-week-sessions-class
  [week-sessions]
  (let [to-class (fn [day yes-value]
                   (if (some #(= day %) week-sessions)
                     yes-value
                     "deselected"))]
    {"Domingo" (to-class "Domingo"  "selected-dark" )
     "Segunda" (to-class "Segunda"  "selected-light")
     "Terça"   (to-class "Terça"    "selected-dark" )
     "Quarta"  (to-class "Quarta"   "selected-light")
     "Quinta"  (to-class "Quinta"   "selected-dark" )
     "Sexta"   (to-class "Sexta"    "selected-light")
     "Sábado"  (to-class "Sábado"   "selected-dark" )}))
(re-frame/reg-sub
  ::redefine-periodization-week-sessions-class
  :<- [::redefine-periodization-week-sessions]
  redefine-periodization-week-sessions-class)

(defn redefine-periodization-week-sessions-toggle
  [{app-state :db} [event week-day]]
  (let [full-week ["Domingo" "Segunda" "Terça" "Quarta" "Quinta" "Sexta" "Sábado"]
        toggle-fn (fn [week-sessions]
                    (if ((set week-sessions) week-day)
                      (remove #{week-day} week-sessions)
                      (filter (-> week-sessions set (conj week-day)) full-week)))
        new-app-state (update-in app-state [:domain :redefine-periodization :week-sessions] toggle-fn)]
    (when (valid-redef-args? new-app-state)
      {:db new-app-state
       :dispatch-n [[::update-timeline] [::reset-sessions] [::update-calendar]]})))
(re-frame/reg-event-fx
  ::redefine-periodization-week-sessions-toggle
  redefine-periodization-week-sessions-toggle)

(defn-traced redefine-periodization-planning-start-event
  [{app-state :db} [event new-start-date]]
  (let [new-app-state (assoc-in app-state [:domain :redefine-periodization :planning-start] new-start-date)]
    (when (valid-redef-args? new-app-state)
      {:db new-app-state
       :dispatch-n [[::update-timeline] [::reset-sessions] [::update-calendar]]})))
(re-frame/reg-event-fx
  ::redefine-periodization-planning-start-event
  redefine-periodization-planning-start-event)

(defn-traced redefine-periodization-planning-end-event
  [{app-state :db} [event new-end-date]]
  (let [new-app-state (assoc-in app-state [:domain :redefine-periodization :planning-end] new-end-date)]
    (when (valid-redef-args? new-app-state)
      {:db new-app-state
       :dispatch-n [[::update-timeline] [::reset-sessions] [::update-calendar]]})))
(re-frame/reg-event-fx
  ::redefine-periodization-planning-end-event
  redefine-periodization-planning-end-event)

(defn redef-prdz-planning-months-duration
  [[planning-start planning-end]]
   (let [total-days (util/days-difference planning-end planning-start)
         months-amount (Math.round (/ total-days 30.5))
         days-reminder (rem total-days 30.5)
         months (if (zero? months-amount) 1 months-amount)]
     (str (when (> days-reminder 0) "+/- ") months (if (> months 1) " meses" " mês"))))
(re-frame/reg-sub
  ::redef-prdz-planning-months-duration
  :<- [::redefine-periodization-planning-start]
  :<- [::redefine-periodization-planning-end]
  redef-prdz-planning-months-duration)

(defn redefine-periodization-planning-duration
  [[planning-start planning-end]]
   (let [total-days (util/days-difference planning-end planning-start)
         months (quot total-days 30.5)
         days (Math.ceil (rem total-days 30.5))
         month-str (str months (if (> months 1) " meses" " mês"))
         days-str (cond
                    (> days 1) (str " e " days " dias")
                    (= days 1) (str " e " days " dia")
                    :else nil)]
     (str month-str days-str)))
(re-frame/reg-sub
  ::redefine-periodization-planning-duration
  :<- [::redefine-periodization-planning-start]
  :<- [::redefine-periodization-planning-end]
  redefine-periodization-planning-duration)

(defn redefine-periodization-session-time-target
  [app-state]
  (util/seconds->minute-text
    (get-in app-state [:domain :redefine-periodization :sessions-time-target])))
(re-frame/reg-sub
  ::redefine-periodization-session-time-target
  redefine-periodization-session-time-target)

(defn-traced redefine-periodization-session-time-target-add
  [{app-state :db} [event to-add]]
  (let [new-app-state (update-in app-state
                                 [:domain :redefine-periodization :sessions-time-target]
                                 + to-add)]
    (when (valid-redef-args? new-app-state)
      {:db new-app-state
       :dispatch-n [[::update-timeline] [::reset-sessions] [::update-calendar]]})))
(re-frame/reg-event-fx
  ::redefine-periodization-session-time-target-add
  redefine-periodization-session-time-target-add)

(defn domain-sessions
  [days-of-training initial-date end-date sessions-time-target]
  (let [total-days (util/days-difference end-date initial-date)
        days-per-micro (/ 28 (count days-of-training))
        micros-amount (Math.floor (/ total-days days-per-micro))]
    (periodization/periodization-sessions (* 4 micros-amount) sessions-time-target)))

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

(defn-traced reset-sessions
  [app-state]
  (let [new-sessions (apply domain-sessions (get-in-redefine-periodization app-state))]
    (assoc-in app-state [:domain :sessions] new-sessions)))
(re-frame/reg-event-db ::reset-sessions reset-sessions)

(defn-traced update-calendar
  [app-state]
  (let [sessions (get-in app-state [:domain :sessions])
        week-sessions (redefine-periodization-week-sessions app-state)
        week-days-of-training (mapv {"Segunda" tick/MONDAY "Terça" tick/TUESDAY "Quarta" tick/WEDNESDAY
                                     "Quinta" tick/THURSDAY "Sexta" tick/FRIDAY "Sábado" tick/SATURDAY
                                     "Domingo" tick/SUNDAY} week-sessions)
        first-day (redefine-periodization-planning-start app-state)
        offset-list #(->> %1 cycle (drop %2) (take (count %1)))
        rearranged-week-days
          (loop [current (util/plus-days first-day 1)
                 index (.indexOf week-days-of-training (tick/day-of-week (tick/date current)))]
            (if (= -1 index)
              (recur (util/plus-days current 1)
                     (.indexOf week-days-of-training (tick/day-of-week (tick/date (util/plus-days current 1)))))
              (offset-list week-days-of-training index)))
        new-calendar-days
          (loop [idx 0
                 val []
                 current-day first-day
                 [current-session & sessions] sessions
                 [current-week-day & week-days] rearranged-week-days]
            (cond
              (nil? current-session) val
              (not= current-week-day (tick/day-of-week (tick/date current-day)))
                (recur idx
                       (conj val {})
                       (util/plus-days current-day 1)
                       (cons current-session sessions)
                       (cons current-week-day week-days))
              :else
                (recur (inc idx)
                       (conj val {:session-idx idx})
                       (util/plus-days current-day 1)
                       sessions
                       (concat week-days [current-week-day]))))]
    (assoc-in app-state [:domain :calendar] {:first-day first-day
                                             :days new-calendar-days})))
(re-frame/reg-event-db ::update-calendar update-calendar)

(def two-line-box
  #js {:display "flex"
       :flexFlow "column"
       :backgroundColor "#ebebeb"
       :alignItems "center"
       :flexGrow 1
       :padding "4px"
       :margin "10px 4px 0 4px"
       :boxShadow "1px 3px 6px 2px #0000003b"
       :cursor "pointer"})

(def adjust-area
  {:backgroundColor "#ebebeb"
   :height "10vh"
   :padding "0 10px"
   :display "flex"
   :flexDirection "column"
   :justifyContent "center"
   :alignItems "center"})

(def adjust-buttons-style
  {:backgroundColor "#ebebeb"
   :height "10vh"
   :display "flex"
   :justifyContent "space-around"
   :alignItems "center"
   :marginBottom "2px"})

(defn two-line-boxes []
  [:div {:style #js {:display @(re-frame/subscribe [::redef-prdz-box-display])
                     :flexWrap "wrap"}}
   [:div {:style #js {:display "flex"
                      :flexGrow 1}}
    [:div
     {:style two-line-box
      :onClick #(re-frame/dispatch [::redef-prdz-box-clicked "editing-goal-level"])}
     [:b @(re-frame/subscribe [::redefine-periodization-goal-txt])]
     [:b @(re-frame/subscribe [::redefine-periodization-level-txt])]]
    [:div
     {:style two-line-box
      :onClick #(re-frame/dispatch [::redef-prdz-box-clicked "editing-time"])}
     [:b @(re-frame/subscribe [::redefine-periodization-session-time-target])]
     [:span "Tempo da Sessão"]]]
   [:div {:style #js {:display "flex"
                      :flexGrow 1}}
    [:div
     {:style two-line-box
      :onClick #(re-frame/dispatch [::redef-prdz-box-clicked "editing-week"])}
     [:b (count @(re-frame/subscribe [::redefine-periodization-week-sessions])) "x"]
     [:span "por Semana"]]
    [:div
     {:style two-line-box
      :onClick #(re-frame/dispatch [::redef-prdz-box-clicked "editing-duration"])}
     [:b @(re-frame/subscribe [::redef-prdz-planning-months-duration])]
     [:span "de Planejamento"]]]])

(defn redefine-periodization []
  [:<>
   [:style
    ".choose-button {
      margin: 0 2px;
      flex: 1;
      padding: 4px;
      box-shadow: 0 1px 1px 0 rgba(0, 0, 0, 0.14),
      0 1px 5px 0 rgba(0, 0, 0, 0.12),
      0 3px 1px -2px rgba(0, 0, 0, 0.2);
      border-radius: 4px;
      cursor: pointer;
    }
    .selected-light{
      background: #35b39d;
      color: white;
    }
    .selected-dark{
      background: #308c7c;
      color: white;
    }
    .deselected{
      background: none;
      color: black;
    }"]
   [two-line-boxes]
   [:div.goal
    {:style (clj->js (merge adjust-area
                            @(re-frame/subscribe [::redef-prdz-level-goal-display])))}
    [:div
     {:style #js {:fontSize "small"
                  :textAlign "center"
                  :color "#555"}}
     "Objetivo"]
    [:div
     {:style #js {:display "flex"
                  :textAlign "center"
                  :alignSelf "stretch"}}
     [:div.choose-button
      {:class (@(re-frame/subscribe [::redefine-periodization-goal])
                                    "Hipertrofia")
       :onClick #(re-frame/dispatch [::redefine-periodization-goal-select "Hipertrofia"])}
      "Hipertrofia"]
     [:div.choose-button
      {:class (@(re-frame/subscribe [::redefine-periodization-goal])
                                    "Força")
       :onClick #(re-frame/dispatch [::redefine-periodization-goal-select "Força"])}
      "Força"]
     [:div.choose-button
      {:class (@(re-frame/subscribe [::redefine-periodization-goal])
                                    "Potência")
       :onClick #(re-frame/dispatch [::redefine-periodization-goal-select "Potência"])}
      "Potência"]
     [:div.choose-button
      {:class (@(re-frame/subscribe [::redefine-periodization-goal])
                                    "Resistência")
       :onClick #(re-frame/dispatch [::redefine-periodization-goal-select "Resistência"])}
      "Resistência"]]]
   [:div.level
    {:style (clj->js (merge adjust-area
                            @(re-frame/subscribe [::redef-prdz-level-goal-display])))}
    [:div
     {:style #js {:fontSize "small"
                  :textAlign "center"
                  :color "#555"}}
     "Nível"]
    [:div
     {:style #js {:display "flex"
                  :textAlign "center"
                  :alignSelf "stretch"}}
     [:div.choose-button
      {:class (@(re-frame/subscribe [::redefine-periodization-level])
                                    "Iniciante")
       :onClick #(re-frame/dispatch [::redefine-periodization-level-select "Iniciante"])}
      "Iniciante"]
     [:div.choose-button
      {:class (@(re-frame/subscribe [::redefine-periodization-level])
                                    "Intermediário")
       :onClick #(re-frame/dispatch [::redefine-periodization-level-select "Intermediário"])}
      "Intermediário"]
     [:div.choose-button
      {:class (@(re-frame/subscribe [::redefine-periodization-level])
                                    "Avançado")
       :onClick #(re-frame/dispatch [::redefine-periodization-level-select "Avançado"])}
      "Avançado"]]]
   [:div.week
    {:style (clj->js (merge adjust-area
                            @(re-frame/subscribe [::redef-prdz-week-display])))}
    [:div
     {:style #js {:fontSize "small"
                  :textAlign "center"
                  :color "#555"}}
     "Sessões na Semana"]
    [:div
     {:style #js {:display "flex"
                  :textAlign "center"
                  :alignSelf "stretch"}}
     [:div.choose-button
      {:class (@(re-frame/subscribe [::redefine-periodization-week-sessions-class])
                                    "Domingo")
       :onClick #(re-frame/dispatch [::redefine-periodization-week-sessions-toggle "Domingo"])}
      "Dom"]
     [:div.choose-button
      {:class (@(re-frame/subscribe [::redefine-periodization-week-sessions-class])
                                    "Segunda")
       :onClick #(re-frame/dispatch [::redefine-periodization-week-sessions-toggle "Segunda"])}
      "Seg"]
     [:div.choose-button
      {:class (@(re-frame/subscribe [::redefine-periodization-week-sessions-class])
                                    "Terça")
       :onClick #(re-frame/dispatch [::redefine-periodization-week-sessions-toggle "Terça"])}
      "Ter"]
     [:div.choose-button
      {:class (@(re-frame/subscribe [::redefine-periodization-week-sessions-class])
                                    "Quarta")
       :onClick #(re-frame/dispatch [::redefine-periodization-week-sessions-toggle "Quarta"])}
      "Qua"]
     [:div.choose-button
      {:class (@(re-frame/subscribe [::redefine-periodization-week-sessions-class])
                                    "Quinta")
       :onClick #(re-frame/dispatch [::redefine-periodization-week-sessions-toggle "Quinta"])}
      "Qui"]
     [:div.choose-button
      {:class (@(re-frame/subscribe [::redefine-periodization-week-sessions-class])
                                    "Sexta")
       :onClick #(re-frame/dispatch [::redefine-periodization-week-sessions-toggle "Sexta"])}
      "Sex"]
     [:div.choose-button
      {:class (@(re-frame/subscribe [::redefine-periodization-week-sessions-class])
                                    "Sábado")
       :onClick #(re-frame/dispatch [::redefine-periodization-week-sessions-toggle "Sábado"])}
      "Sab"]]]
    [:div.adjust-session-time
     {:style #js {:backgroundColor "#ebebeb"
                  :height "10vh"
                  :display @(re-frame/subscribe [::redef-prdz-time-display])
                  :grid "2fr 1fr / 1fr 1fr 1fr"}}
     [:span
      {:style #js {:gridArea "1 / 1 / 2 / 4"
                   :placeSelf "end center"
                   :fontSize "1.17em"}}
      "Tempo Médio por Sessão"]
     [:span
      {:style #js {:gridArea "2 / 1"
                   :placeSelf "center end"
                   :backgroundColor "white"
                   :borderRadius "30px"
                   :width "30px"
                   :height "30px"
                   :fontSize "3em"
                   :boxShadow "1px 1px 2px 0px black"
                   :display "flex"
                   :justifyContent "center"
                   :alignItems "center"
                   :cursor "pointer"}
       :onClick #(re-frame/dispatch [::redefine-periodization-session-time-target-add -300])}
      [:span {:style #js {:paddingBottom "7px"}} "-"]]
     [:b
      {:style #js {:gridArea "2 / 2"
                   :placeSelf "center"
                   :fontSize "1.57em"}}
      @(re-frame/subscribe [::redefine-periodization-session-time-target])]
     [:span
      {:style #js {:gridArea "2 / 3"
                   :placeSelf "center start"
                   :backgroundColor "white"
                   :borderRadius "30px"
                   :width "30px"
                   :height "30px"
                   :textAlign "center"
                   :fontSize "2em"
                   :boxShadow "1px 1px 2px 0px black"
                   :display "flex"
                   :justifyContent "center"
                   :alignItems "center"
                   :cursor "pointer"}
       :onClick #(re-frame/dispatch [::redefine-periodization-session-time-target-add 300])}
      [:span {:style #js {:paddingBottom "2px"}} "+"]]]
    [:div.duration-selector
     {:style #js {:backgroundColor "#ebebeb"
                  :height "10vh"
                  :padding "0 10px"
                  :display @(re-frame/subscribe [::redef-prdz-duration-display])
                  :justifyContent "space-evenly"
                  :alignItems "center"}}
     [(util/with-mount-fn
        [:vaadin-date-picker
         {:label "Início do planejamento"
          :value @(re-frame/subscribe [::redefine-periodization-planning-start])
          :component-did-mount
          (fn [this]
            (set! (-> (reagent/dom-node this) .-onchange)
                  #(re-frame/dispatch [::redefine-periodization-planning-start-event
                                       (-> % .-target .-value)])))}])]
     [(util/with-mount-fn
        [:vaadin-date-picker
         {:label "Final do planejamento"
          :value @(re-frame/subscribe [::redefine-periodization-planning-end])
          :component-did-mount
          (fn [this]
            (set! (-> (reagent/dom-node this) .-onchange)
                  #(re-frame/dispatch [::redefine-periodization-planning-end-event
                                       (-> % .-target .-value)])))}])]]
    [:div.duration-text
     {:style #js {:backgroundColor "#ebebeb"
                  :height "8vh"
                  :padding "0 10px"
                  :display @(re-frame/subscribe [::redef-prdz-duration-display])
                  :flexDirection "column"
                  :alignItems "center"}}
     [:div
      {:style #js {:fontSize "small"
                   :textAlign "center"
                   :color "#555"}}
      "Duração do Planejamento"]
     [:div
      {:style #js {:fontSize "medium"
                   :textAlign "center"
                   :color "black"}}
      @(re-frame/subscribe [::redefine-periodization-planning-duration])]]
    [:div
     {:style (clj->js
               (merge adjust-buttons-style
                      @(re-frame/subscribe [::redef-prdz-buttons-display])))}
     [:> material-Button
      {:style #js {:backgroundColor "#3bbcb7"
                   :color "white"}
       :variant "contained"
       :onClick #(re-frame/dispatch [::redef-prdz-edit-cancel])}
      "Cancelar"]
     [:> material-Button
      {:style #js {:width "110px"
                   :backgroundColor "#3bbcb7"
                   :color "white"}
       :onClick #(re-frame/dispatch [::redef-prdz-edit-ok])
       :variant "contained"}
      "Ok"]]])
