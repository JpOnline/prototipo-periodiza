(ns pr4.util
  (:require
    [clojure.spec.alpha :as spec]
    [tick.alpha.api :as tick]
    [reagent.core :as reagent]
    )
  )

(defn round-to-precision
  "it takes an argument for which number to round,
  the same as the classic round had the implicit
  argument of 1 (e.g. (round 2.3) is the same as
  (round-to-precision 2.3 1)"
  [n & [precision]]
  (let [temp (+ n (if precision (/ precision 2) 0.5))]
    (- temp (mod temp (or precision 1)))))

(defn between? [value [min max]]
  (and (>= value min) (< value max)))

(defn pre-validation [spec input message]
  "Throw a message when the input don't conform to the spec."
  (or (spec/valid? spec input)
      (throw (ex-info message {:spec-input input
                               :spec-data (spec/explain-data spec input)}))))

(defn validate-interval [coll [i-1 i-2]]
  (or (<= i-1 i-2 (count coll))
      (throw (ex-info "Interval not in coll range"
                      {:coll coll :interval [i-1 i-2]}))))

(defn select-indexes [coll interval]
  ;; {:pre [(validate-interval coll interval)]}
  (if (<= (second interval) (count coll))
    (map #(nth coll %) (apply range interval))
    [(last coll)]))

(defn seconds->minute-text [seconds]
  (let [hour (quot seconds 3600)
        hour-str (when (> hour 0) (str hour ":"))
        min (-> seconds (mod 3600) (quot 60))
        sec (mod seconds 60)]
    (str hour-str (when (< min 10) "0") min ":" (when (< sec 10) "0") sec)))

(defn first-filter [f coll]
  (some #(if (f %) %) coll))

(defn days-difference [end start]
  (-> (tick/new-interval (tick/date start) (tick/date end))
      (tick/divide (tick/new-duration 1 :days))
      count
      dec))

(defn with-mount-fn
  "Wrap component in the create-class fn so the react component-did-mount
  hook can be used."
  [[n {:keys [component-did-mount]} :as to-render]]
  (reagent/create-class
    {:reagent-render #(into [] (update-in to-render [1]
                                          dissoc :component-did-mount))
     :component-did-mount component-did-mount}))

(defn plus-days [date days-to-add]
  (str (tick/+ (tick/date date) (tick/new-period days-to-add :days))))
