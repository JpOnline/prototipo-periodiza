(ns ^:figwheel-hooks pr4.core
  (:require
    [pr4.landing :as landing]
    [goog.dom :as gdom]
    [pr4.external-storage :as ext-store]
    [re-frame.core :as re-frame]
    [reagent.core :as reagent :refer [atom]]))

(defn get-app-element []
  (gdom/getElement "app"))

(defn mount [el]
  (reagent/render-component [landing/main] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(do
  (re-frame/clear-subscription-cache!)
  (ext-store/init-app-state "none")
  (mount-app-element))

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
