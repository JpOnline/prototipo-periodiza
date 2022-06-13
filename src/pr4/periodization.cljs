(ns pr4.periodization
  (:require
    [pr4.util :as util]
    [clojure.spec.gen.alpha :as gen]
    [clojure.spec.alpha :as spec]))

(spec/def ::between-1-and-30 (spec/and int? #(> % 0) #(<= % 30)))
(spec/def ::percentage (spec/and #(>= % 0) #(<= % 100)))

(defn pre-validation [spec input message]
  "Throw a message when the input don't conform to the spec."
  (or (spec/valid? spec input)
      (throw (ex-info message (spec/explain-data spec input)))))

;; It needs validation because is user input
(defn reps-to-intensity
  "Returns the percentage of 1RM associated with the number of repetitions
  accomplished."
  [max-reps]
  {:pre [(pre-validation
           ::between-1-and-30
           max-reps
           "Repetições deve ser entre 1 e 30.")]
   :post [(spec/valid? ::percentage %)]}
  (let [intensity-list [0 100 95 93 90 87 85 83 80 77 75 70 67.5 67 66 65 63.3
                        61.7 60 58.3 56.7 55 53.3 51.7 50 48.3 46.7 45 43.3
                        41.7 40]]
    (nth intensity-list max-reps)))

(spec/fdef reps-to-intensity
           :args (spec/cat :max-reps ::between-1-and-30)
           :ret int?)

(defn get-1rm [reps weight]
  "Assuming the practitioner went to exhaustion,
   it returns the maximum weight that he/she would do."
  (/ (* 100 weight) (reps-to-intensity reps)))

(defn session-volume [reps sets]
  (-> reps reps-to-intensity (* reps) (* sets)))

(defn session->total-volume
  "Uses repetitions of an exercise, calculating the intensity of this
  reps in % of RM (max repetition) and sets to output the range of
  total volume in % of 1 RM."
  [{:keys [reps-min reps-max total-sets]}]
  (let [vol-min (Math.round (session-volume reps-min total-sets))
        vol-max (Math.round (session-volume reps-max total-sets))]
    (if (= reps-min reps-max)
      (str vol-min "%")
      (str vol-min "-" vol-max "%"))))

(defn execution-time
  "Receives a session with reps, sets and rest.
  Suppose each repetition takes 3 seconds
  Returns a the execution time estimation in seconds"
  [reps sets]
  (-> reps (* 3) (* sets)))

(defn total-rest-time [sets rest-per-set]
  (* rest-per-set (dec sets)))

(defn session->time-estimate
  "Receives a session with a: range of reps, sets and rest-time.
  Returns a text representing the time estimation of the session in
  the format mm:ss"
  [{:keys [reps-min reps-max rest-time total-sets]}]
  (+ (execution-time reps-max total-sets)
     (total-rest-time total-sets rest-time)))

(def micros-models
  {
   ;; Exemplo de micro com 3 sessões
   [{:reps-min 1 :reps-max 3 :total-sets 18}
    {:reps-min 1 :reps-max 3 :total-sets 18}
    {:reps-min 1 :reps-max 3 :total-sets 18}]
   "Ordinário"

   [{:reps-min 12 :reps-max 15 :rest-time 45} ;; 50%
    {:reps-min 10 :reps-max 12 :rest-time 60} ;; 60%
    {:reps-min 11 :reps-max 14 :rest-time 45} ;; 55%
    {:reps-min 9 :reps-max 11 :rest-time 60} ;; 65%
    ]
   "Incorporação"

   [{:reps-min 8 :reps-max 10 :rest-time 75} ;; 70%
    {:reps-min 10 :reps-max 12 :rest-time 60} ;; 60%
    {:reps-min 12 :reps-max 15 :rest-time 45} ;; 50%
    {:reps-min 13 :reps-max 15 :rest-time 45}] ;; 40%
   "Ordinário"

   [{:reps-min 6 :reps-max 8 :rest-time 90} ;; 80%
    {:reps-min 4 :reps-max 6 :rest-time 105} ;; 90%
    {:reps-min 3 :reps-max 5 :rest-time 120} ;; 100%
    {:reps-min 6 :reps-max 8 :rest-time 90}] ;; 80%
   "Choque 1"

   [{:reps-min 6 :reps-max 8 :rest-time 90} ;; 80%
    {:reps-min 10 :reps-max 12 :rest-time 60} ;; 60%
    {:reps-min 4 :reps-max 6 :rest-time 105} ;; 90%
    {:reps-min 3 :reps-max 5 :rest-time 120}] ;; 100%
   "Choque 2"

   [{:reps-min 8 :reps-max 10 :rest-time 75} ;; 70%
    {:reps-min 13 :reps-max 15 :rest-time 45} ;; 40%
    {:reps-min 10 :reps-max 12 :rest-time 60} ;; 60%
    {:reps-min 14 :reps-max 15 :rest-time 45} ;; 30%
    ]
   "Recuperação"
   })

(spec/fdef count-phases
           :args (spec/cat :micros-amount nat-int? :debugging? #{true})
           :fn (fn [{{:keys [micros-amount]} :args ret :ret}]
                 (cond
                   (< micros-amount 3) true
                   :else  (= micros-amount (reduce + ret)))))

(defn count-phases
  "Calculate size of the phases basic, specific and
  transition according to the number of microcycles."
  [micros-amount & [debugging?]]
  (let [basic-micros1 (Math.round (* 0.55 micros-amount))
        specific-micros1 (Math.round (* 0.45 micros-amount))
        transition-micros1 (- micros-amount basic-micros1 specific-micros1)

        ;; We have at least 1 transitional microcycle
        [basic-micros2 specific-micros2 transition-micros2]
        (loop [basic-micros basic-micros1
               specific-micros specific-micros1
               transition-micros transition-micros1
               i 0]
          (cond
            (>= i (- 1 transition-micros)) [basic-micros specific-micros transition-micros]
            (= 0 (mod i 2)) (recur (dec basic-micros) specific-micros (inc transition-micros) (inc i))
            ;; (= 1 (mod i 2))
            :else (recur basic-micros (dec specific-micros) (inc transition-micros) (inc i))))

        ;; The last mesocycle of each fase must have 3, 4 or 5 microcycles
        [basic-micros specific-micros3] (if (= 2 (mod basic-micros2 4))
                                          [(dec basic-micros2) (inc specific-micros2)]
                                          [basic-micros2 specific-micros2])
        [specific-micros transition-micros] (if (= 2 (mod specific-micros3 4))
                                              [(dec specific-micros3) (inc transition-micros2)]
                                              [specific-micros3 transition-micros2])]
    (when debugging?
      (let [last-micro #(case (mod % 4)
                          0 4
                          1 5
                          2 "Should not happen"
                          3 3
                          "Impossible case")]
        (println (str "Microciclos: " micros-amount))
        (println (str (-> basic-micros (/ micros-amount) (* 100) (/ 1) Math.floor)
                             "% Basicos: " basic-micros
                             ", ultimo micro: " (last-micro basic-micros)))
        (println (str (-> specific-micros (/ micros-amount) (* 100) (/ 1) Math.floor)
                             "% Especifica: " specific-micros
                             ", ultimo micro: " (last-micro specific-micros)))
        (println (str (-> transition-micros (/ micros-amount) (* 100) (/ 1) Math.floor)
                             "% Transicao: " transition-micros))))
    [basic-micros specific-micros transition-micros]))

(def micro-category?
  #{"Incorporação"
    "Ordinário"
    "Choque 1"
    "Choque 2"
    "Recuperação"})

(defn validate-micro-sessions-input [sessions-amount time-length category]
  (cond
    (not (#{4 5 6} sessions-amount))
      (throw (ex-info "Only micros of 4 5 or 6 sessions can be created."
                      {:sessions-amount sessions-amount}))
    (<= time-length 0)
      (throw (ex-info "Session time must be greater than 0."
                      {:time-length time-length}))
    (not (micro-category? category))
      (throw (ex-info "Could not recognize this microcycle category."
                      {:category category}))
    :else true))

(spec/fdef micro-sessions
           :args (spec/cat :sessions-amount #{4}
                           :time-length (spec/and int? #(> 7200 % 600))
                           :category micro-category?))

(defn micro-sessions
  "Returns a list of sessions in the format of
  {:reps-min :reps-max :rest-time :total-sets}"
  [sessions-amount time-length category]
  {:pre [(validate-micro-sessions-input sessions-amount time-length category)]}
  (let [chosen-micros-models (filter #(-> % val ((partial = category)))
                                     micros-models)
        chosen-model (->> chosen-micros-models
                          (map first)
                          (util/first-filter
                            #(-> %
                                 count
                                 ((partial = sessions-amount)))))
        add-sets-based-on-time (fn [{:keys [reps-max rest-time] :as session}]
                                 (assoc session :total-sets
                                        (Math.round
                                          (/ (+ time-length rest-time)
                                             (+ (* 3 reps-max) rest-time)))))]
    (map add-sets-based-on-time chosen-model)))

(defn meso-micros
  "Returns a list of micros category"
  [micros-amount category]
  ;; choice table has the category as columns and micros-amount as rows so
  ;; when choosing the "Estabilizador" 4, for example, it returns the
  ;; ["Ordinário" "Ordinário" "Choque 1" "Recuperação"]
  (let [choice-table {"Estabilizador" {3 ["Ordinário" "Choque 1" "Recuperação"]
                                       4 ["Ordinário" "Ordinário" "Choque 1" "Recuperação"]
                                       5 ["Ordinário" "Ordinário" "Ordinário" "Choque 1" "Recuperação"]}
                      "Controle"      {3 ["Choque 1" "Choque 1" "Recuperação"]
                                       4 ["Choque 1" "Recuperação" "Choque 1" "Recuperação"]
                                       5 ["Ordinário" "Choque 1" "Recuperação" "Choque 1" "Recuperação"]}
                      "Auge"          {3 ["Choque 1" "Choque 2" "Recuperação"]
                                       4 ["Choque 1" "Choque 2" "Recuperação" "Recuperação"]
                                       5 ["Choque 1" "Choque 1" "Choque 2" "Recuperação" "Recuperação"]}
                      "Recuperativo"  {1 ["Recuperação"]
                                       2 ["Recuperação" "Recuperação"]}
                      "Incorporativo" {3 ["Incorporação" "Ordinário" "Recuperação"]
                                       4 ["Incorporação" "Ordinário" "Ordinário" "Recuperação"]
                                       5 ["Incorporação" "Ordinário" "Ordinário" "Ordinário" "Recuperação"]}
                      "Base"          {3 ["Ordinário" "Choque 1" "Recuperação"]
                                       4 ["Ordinário" "Ordinário" "Choque 1" "Recuperação"]
                                       5 ["Ordinário" "Ordinário" "Ordinário" "Choque 1" "Recuperação"]}}]
    ((choice-table category) micros-amount)))

(defn phase-mesos
  "Returns a list of mesos in the format of
  {:micros-amount :category}
  E.g. [{:micros-amount 4 :category Incorporativo}
  {:micros-amount 5 :category Estabilizador}]"
  [micros-amount category]
  (if (= category "Transição")
    [{:micros-amount micros-amount :category "Recuperativo"}]
    (let [add-basic-meso (fn [amount creation-order meso-list]
                           ;; Depending the order the mesos are created, they can be put in
                           ;; the middle of the phase.
                           (case creation-order
                             0 [{:micros-amount amount :category "Incorporativo"}]
                             1 (into meso-list [{:micros-amount amount :category "Estabilizador"}])
                             ;; >= 2
                             (into [] (concat (butlast meso-list) [{:micros-amount amount :category "Base"}] [(last meso-list)]))))
          add-specific-meso (fn [amount creation-order meso-list]
                              ;; Depending the order the mesos are created, they can be put in
                              ;; the middle of the phase.
                              (case creation-order
                                0 [{:micros-amount amount :category "Auge"}]
                                1 (into [] (concat (butlast meso-list) [{:micros-amount amount :category "Controle"}] [(last meso-list)]))
                                2 (into [] (concat (butlast meso-list) [{:micros-amount amount :category "Estabilizador"}] [(last meso-list)]))
                                ;; >= 3
                                (into [] (concat (take (- creation-order 2) meso-list) [{:micros-amount amount :category "Base"}] (drop (- creation-order 2) meso-list)))))
          add-meso ({"Básica" add-basic-meso
                     "Específica" add-specific-meso}
                    category)]
      (loop [micros-rest micros-amount
             meso-list []
             creation-order 0]
        (if (<= micros-rest 5)
          (add-meso micros-rest creation-order meso-list)
          (recur (- micros-rest 4)
                 (add-meso 4 creation-order meso-list)
                 (inc creation-order)))))))

(spec/fdef periodization-sessions
           :args (spec/cat :sessions-amount (spec/with-gen (spec/and int? #(> 100 % 27))
                                              #(gen/choose 27 500)))
           :fn (fn [{{:keys [sessions-amount]} :args ret :ret}]
                 (= (count ret) (* 4 (quot sessions-amount 4)))))

(defn periodization-sessions
  "Uses the total number of sessions to return the list of sessions
  according to a periodization."
  [sessions-amount sessions-time-target]
  (let [sessions-reminder (mod sessions-amount 4)]
    ;; Good for debugging
    #_{:total sessions-amount
     :fase (count-phases (quot sessions-amount 4))
     :reminder sessions-reminder}
    (->> sessions-amount
         (#(quot % 4))
         count-phases
         (mapcat #(phase-mesos %2 %1) ["Básica" "Específica" "Transição"])
         (map vals)
         (mapcat #(apply meso-micros %))
         (mapcat #(micro-sessions 4 sessions-time-target %))
         #_(map #(assoc % :time (/ (session->time-estimate %) 60))))))

(comment
  (require '[clojure.spec.test.alpha :as stest])
  (require '[clojure.spec.gen.alpha :as gen])
  (stest/instrument)
  (stest/unstrument)

  (cljs.pprint/pprint (spec/exercise-fn `macrocycle-timeline))
  (cljs.pprint/pprint (spec/exercise-fn `periodization-sessions))
  (stest/summarize-results (cljs.pprint/pprint (stest/check `count-phases)))
  (stest/summarize-results (cljs.pprint/pprint (stest/check `periodization-sessions)))
  (cljs.pprint/pprint (periodization-sessions 200))
  (cljs.pprint/pprint (gen/sample (spec/gen ::dispatch-an-event-args)))
  (cljs.pprint/pprint (gen/sample (spec/gen (spec/inst-in #inst "2019-08-01" #inst "2020-08-01"))))

  (nth (iterate (macrocycle-timeline [tick/MONDAY tick/WEDNESDAY tick/FRIDAY] "2019-08-01" "2019-12-30") "2019-08-01") 1)
  )
