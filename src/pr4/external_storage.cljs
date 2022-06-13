(ns pr4.external-storage
  (:require
    firebase
    [re-frame.core :as re-frame]
    [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
    [pr4.app-state :as app-state]
    ))

(def firebaseConfig
  #js {:apiKey "AIzaSyCBagO0y2sPLcng_r39NqVJNkocgbNWw"
       :authDomain "pr4-periodiza.firebaseapp.com",
       :databaseURL "https://pr4-periodiza.firebaseio.com",
       :projectId "pr4-periodiza",
       :storageBucket "pr4-periodiza.appspot.com",
       :messagingSenderId "209599502229",
       :appId "1:209599502229:web:7e8dc961c444d840"})

(def firebase-db
  (do
    (when (= 0 (-> firebase .-apps .-length))
      (-> firebase (.initializeApp firebaseConfig)))
    (-> firebase (.firestore))))

(defn firebase-save [app-state-map user]
  (let [json (clj->js app-state-map)]
    (assert
      (= app-state-map (js->clj json :keywordize-keys true))
      "The given map is different if converted back from JSON.")
    (-> firebase-db
        (.collection "users")
        (.doc user)
        (.set json))))

(re-frame/reg-sub ::domain #(get % :domain))

(defn firebase-el [{:keys [user]}]
  (let [domain-map @(re-frame/subscribe [::domain])]
    [:img {:src domain-map
           :onError #(firebase-save domain-map user)
           :style #js {:display "none"}}]))

(defn init-app-state [user]
  (re-frame/dispatch-sync
    [::initialize-app-state
     app-state/default-app-state])
  (-> firebase-db (.collection "users") (.doc user) (.get)
      (.then (fn [doc]
               (re-frame/dispatch-sync
                 [::initialize-app-state
                  (if (.-exists doc)
                    {:ui (:ui app-state/default-app-state)
                     :domain (js->clj (-> doc (.data)) :keywordize-keys true)}
                    app-state/default-app-state)])
               ;; (re-frame/dispatch-sync [:pr4.prototipo/update-timeline])
               (re-frame/dispatch-sync [:pr4.components.macro-timeline/update-timeline])))
      (.catch (fn [error]
                (js/console.log "Error getting firebase document:" error)))))

(re-frame/reg-event-db ::initialize-app-state
  (fn-traced [_ [event application-state]]
             application-state))
