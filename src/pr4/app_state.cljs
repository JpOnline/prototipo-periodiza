(ns pr4.app-state
  (:require
    [re-frame.core :as re-frame]
    [tick.alpha.api :as tick]
    [clojure.spec.alpha :as spec]
    [pr4.util :as util]))

;; (def today "2021-05-09")
(def week-days-of-training [tick/TUESDAY tick/WEDNESDAY tick/FRIDAY])
(def default-sessions
  [{:reps-min 12, :reps-max 15, :total-sets 15, :rest-time 45}
   {:reps-min 10, :reps-max 12, :total-sets 15, :rest-time 60}
   {:reps-min 11, :reps-max 14, :total-sets 15, :rest-time 45}
   {:reps-min 9, :reps-max 11, :total-sets 15, :rest-time 60}
   {:reps-min 8, :reps-max 10, :total-sets "14", :rest-time 75}
   {:reps-min 10, :reps-max 12, :total-sets "14", :rest-time 60}
   {:reps-min 12, :reps-max 15, :total-sets "14", :rest-time 45}
   {:reps-min 13, :reps-max 15, :total-sets "14", :rest-time 45}
   {:reps-min 8, :reps-max 10, :total-sets "13", :rest-time 75}
   {:reps-min 10, :reps-max 12, :total-sets "13", :rest-time 60}
   {:reps-min 12, :reps-max 15, :total-sets "13", :rest-time 45}
   {:reps-min 13, :reps-max 15, :total-sets "13", :rest-time 45}
   {:reps-min 8, :reps-max 10, :total-sets "12", :rest-time 75}
   {:reps-min 13, :reps-max 15, :total-sets "12", :rest-time 45}
   {:reps-min 10, :reps-max 12, :total-sets "12", :rest-time 60}
   {:reps-min 14, :reps-max 15, :total-sets "12", :rest-time 45}
   {:reps-min 8, :reps-max 10, :total-sets "13", :rest-time 75}
   {:reps-min 10, :reps-max 12, :total-sets "13", :rest-time 60}
   {:reps-min 12, :reps-max 15, :total-sets "13", :rest-time 45}
   {:reps-min 13, :reps-max 15, :total-sets "13", :rest-time 45}
   {:reps-min 6, :reps-max 8, :total-sets "19", :rest-time 90}
   {:reps-min 4, :reps-max 6, :total-sets "19", :rest-time 105}
   {:reps-min 3, :reps-max 5, :total-sets "19", :rest-time 120}
   {:reps-min 6, :reps-max 8, :total-sets "19", :rest-time 90}
   {:reps-min 8, :reps-max 10, :total-sets 15, :rest-time 75}
   {:reps-min 13, :reps-max 15, :total-sets 15, :rest-time 45}
   {:reps-min 10, :reps-max 12, :total-sets 15, :rest-time 60}
   {:reps-min 14, :reps-max 15, :total-sets 15, :rest-time 45}
   {:reps-min 6, :reps-max 8, :total-sets 15, :rest-time 90}
   {:reps-min 4, :reps-max 6, :total-sets 15, :rest-time 105}
   {:reps-min 3, :reps-max 5, :total-sets 15, :rest-time 120}
   {:reps-min 6, :reps-max 8, :total-sets 15, :rest-time 90}
   {:reps-min 8, :reps-max 10, :total-sets "14", :rest-time 75}
   {:reps-min 13, :reps-max 15, :total-sets "14", :rest-time 45}
   {:reps-min 10, :reps-max 12, :total-sets "14", :rest-time 60}
   {:reps-min 14, :reps-max 15, :total-sets "14", :rest-time 45}
   {:reps-min 6, :reps-max 8, :total-sets "18", :rest-time 90}
   {:reps-min 4, :reps-max 6, :total-sets "18", :rest-time 105}
   {:reps-min 3, :reps-max 5, :total-sets "18", :rest-time 120}
   {:reps-min 6, :reps-max 8, :total-sets "18", :rest-time 90}
   {:reps-min 6, :reps-max 8, :total-sets 15, :rest-time 90}
   {:reps-min 10, :reps-max 12, :total-sets 15, :rest-time 60}
   {:reps-min 4, :reps-max 6, :total-sets 15, :rest-time 105}
   {:reps-min 3, :reps-max 5, :total-sets 15, :rest-time 120}
   {:reps-min 8, :reps-max 10, :total-sets "13", :rest-time 75}
   {:reps-min 13, :reps-max 15, :total-sets "13", :rest-time 45}
   {:reps-min 10, :reps-max 12, :total-sets "13", :rest-time 60}
   {:reps-min 14, :reps-max 15, :total-sets "13", :rest-time 45}
   {:reps-min 8, :reps-max 10, :total-sets 15, :rest-time 75}
   {:reps-min 13, :reps-max 15, :total-sets 15, :rest-time 45}
   {:reps-min 10, :reps-max 12, :total-sets 15, :rest-time 60}
   {:reps-min 14, :reps-max 15, :total-sets 15, :rest-time 45}
   ])
(def default-microcycles
  (->> (count default-sessions)
       range
       (partition-all 4)
       (map last)
       (mapv inc)))

(defn create-calendar [sessions week-sessions first-day]
  (let [week-days-of-training (mapv {"Segunda" tick/MONDAY "Terça" tick/TUESDAY "Quarta" tick/WEDNESDAY
                                     "Quinta" tick/THURSDAY "Sexta" tick/FRIDAY "Sábado" tick/SATURDAY
                                     "Domingo" tick/SUNDAY} week-sessions)
        offset-list #(->> %1 cycle (drop %2) (take (count %1)))
        rearranged-week-days
          (loop [current (util/plus-days first-day 1)
                 index (.indexOf week-days-of-training (tick/day-of-week (tick/date current)))]
            (if (= -1 index)
              (recur (util/plus-days current 1)
                     (.indexOf week-days-of-training (tick/day-of-week (tick/date (util/plus-days current 1)))))
              (offset-list week-days-of-training index)))]
    {:first-day first-day
     :days (loop [idx 0
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
                        (concat week-days [current-week-day]))))}))

(defn sessions-micros-synced? [{:keys [sessions microcycles]}]
  (= (count sessions) (last microcycles))) ;; They correspond to the same number of sessions.

(spec/def ::valid-app-state
  (spec/keys :req-un [::ui ::domain]))

(spec/def ::ui
  (spec/keys :req-un [::session-selection ::increment-scale]))

(spec/def ::session-selection
  (spec/tuple nat-int? nat-int?))

(spec/def ::domain
  (spec/and (spec/keys :req-un [::sessions ::microcycles ::calendar])
            sessions-micros-synced?))

(defn default-first-day [first-option]
  (as-> first-option $
    (util/days-difference (tick/date) $)
    (- $ 60)
    (util/round-to-precision $ 120)
    (tick/new-period $ :days)
    (tick/+ (tick/date first-option) $)
    (str $)))

(def default-redefine-periodization
  {:goal "Força"
   :level "Intermediário"
   :week-sessions ["Segunda" "Terça" "Quinta" "Sexta"]
   :planning-start (default-first-day "2019-05-19")
   :planning-end (util/plus-days (default-first-day "2019-05-19") 120)
   :sessions-time-target 1800})

(def default-app-state
  {:ui {:session-selection [0 4]
        :increment-scale 0
        :calendar-selection [0 1]}
   :domain {:sessions default-sessions
            :microcycles default-microcycles
            :calendar (create-calendar
                        default-sessions
                        ["Segunda" "Terça" "Quinta" "Sexta"]
                        (default-first-day "2019-05-19"))
            ;; The redefine structure actually belongs to the :ui, it's here
            ;; just to be saved in the database.
            ;; TODO: move the redefine-periodization structure to the :ui and
            ;; setup it to also be saved in the external storage.
            :redefine-periodization default-redefine-periodization
            }})

(defn sessions
  [app-state]
  (get-in app-state [:domain :sessions]))
(re-frame/reg-sub
  ::sessions
  sessions)

(defn microcycles
  [app-state]
  (get-in app-state [:domain :microcycles]))
(re-frame/reg-sub
  ::microcycles
  microcycles)

(defn idx-micros
  "Output example:
    {0 0, 32 11, 3 1, 6 2, 9 3, 12 4, 15 5, 18 6, 21 7, 24 8, 27 9, 30 10}"
  [micros]
  (into {0 0} (map-indexed (fn [i v] [v (inc i)]) micros)))
(re-frame/reg-sub
  ::idx-micros
  :<- [::microcycles]
  idx-micros)

(defn session-selection
  [app-state]
  (get-in app-state [:ui :session-selection]))
(re-frame/reg-sub
  ::session-selection
  session-selection)

(defn increment-scale
  [app-state]
  (get-in app-state [:ui :increment-scale]))
(re-frame/reg-sub
  ::increment-scale
  increment-scale)

(defn calendar-raw
  [app-state]
  (get-in app-state [:domain :calendar]))
(re-frame/reg-sub
  ::calendar-raw
  calendar-raw)

(defn next-day [current-day]
  (str (tick/+ (tick/date current-day) (tick/new-period 1 :days))))

;; e.g. [{:date "2019-06-14 :session-idx 3} {:date "2019-06-13" :absence true}]
(defn calendar
  [{:keys [first-day days]}]
  (map #(assoc %1 :date %2)
       days
       (iterate next-day first-day)))
(re-frame/reg-sub
  ::calendar
  :<- [::calendar-raw]
  calendar)

(defn absent-coll
  [{:keys [days]}]
  (map :absent? days))
(re-frame/reg-sub
  ::absent-coll
  :<- [::calendar-raw]
  absent-coll)

(defn calendar-selection [app-state]
  (get-in app-state [:ui :calendar-selection]))
(re-frame/reg-sub ::calendar-selection calendar-selection)

(defn absent?
  [[{:keys [days]} [sel-start sel-end]]]
  (:absent? (nth days sel-start)))
(re-frame/reg-sub
  ::absent?
  :<- [::calendar-raw]
  :<- [::calendar-selection]
  absent?)

(defn first-calendar-day
  [app-state]
  (get-in app-state [:domain :calendar :first-day]))
(re-frame/reg-sub
  ::first-calendar-day
  first-calendar-day)

(defn today-date [app-state] (str (tick/date)))
(re-frame/reg-sub ::today today-date)

(defn session-state
  [app-state]
  (get-in app-state [:ui :session-state]))
(re-frame/reg-sub
  ::session-state
  session-state)

(defn set-inten-boxes-display
  [session-state]
  (case session-state
    "editing-sets" "none"
    "editing-intensity" "none"
    "flex"))
(re-frame/reg-sub
  ::set-inten-boxes-display
  :<- [::session-state]
  set-inten-boxes-display)

(defn adjust-sets-display
  [session-state]
  (case session-state
    "editing-sets" "grid"
    "none"))
(re-frame/reg-sub
  ::adjust-sets-display
  :<- [::session-state]
  adjust-sets-display)

(defn adjust-intensity-display
  [session-state]
  (case session-state
    "editing-intensity" "grid"
    "none"))
(re-frame/reg-sub
  ::adjust-intensity-display
  :<- [::session-state]
  adjust-intensity-display)

(defn adjust-rest-time-display
  [session-state]
  (case session-state
    "editing-sets" "grid"
    "editing-intensity" "grid"
    "none"))
(re-frame/reg-sub
  ::adjust-rest-time-display
  :<- [::session-state]
  adjust-rest-time-display)

(defn adjust-buttons-display
  [session-state]
  (case session-state
    "editing-sets" "flex"
    "editing-intensity" "flex"
    "none"))
(re-frame/reg-sub
  ::adjust-buttons-display
  :<- [::session-state]
  adjust-buttons-display)

(defn macrocycle-timeline
  [app-state]
  (get-in app-state [:ui :macrocycle-timeline]))
(re-frame/reg-sub
  ::macrocycle-timeline
  macrocycle-timeline)

