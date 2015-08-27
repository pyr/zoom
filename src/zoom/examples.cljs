(ns zoom.examples
  (:require [om.core         :as om :include-macros true]
            [om.dom          :as d  :include-macros true]
            [cljs.core.async :refer [timeout <!]]
            [zoom.box        :refer [box]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(defonce app-state
  (atom
   {:gauges     [{:title "Page Views"
                  :gauge "12,938"
                  :summary "5%"
                  :summary-direction "up"
                  :box-class "zbox-green"}
                 {:title "Downloads"
                  :gauge "758"
                  :summary "1.3%"
                  :box-class "zbox-red"}
                 {:title "Sign-Ups"
                  :gauge "1,293"
                  :summary "6.75%"
                  :summary-direction "up"
                  :box-class "zbox-purple"}
                 {:title "Sales"
                  :gauge "8"
                  :summary "1.3%"
                  :summary-direction "down"
                  :box-class "zbox-yellow"}]
    :sparklines [{:title "Page Views"
                  :gauge "12,938"
                  :summary "5%"
                  :summary-direction "up"
                  :bezier? false
                  :fill? false
                  :content :spark
                  :data [28 68 41 43 96 45 100]
                  :box-class "zbox-green"}
                 {:title "Downloads"
                  :gauge "758"
                  :summary "1.3%"
                  :fill? true
                  :bezier? false
                  :summary-direction "down"
                  :content :spark
                  :data [28 68 41 43 96 45 100]
                  :box-class "zbox-red"}
                 {:title "Sign-Ups"
                  :gauge "1,293"
                  :summary "6.75%"
                  :summary-direction "up"
                  :curve-tension 0.4
                  :bezier? true
                  :fill? false
                  :content :spark
                  :data [28 68 41 43 96 45 100]
                  :box-class "zbox-purple"}
                 {:title "Sales"
                  :gauge "8"
                  :summary "1.3%"
                  :summary-direction "down"
                  :curve-tension 0.4
                  :bezier? true
                  :fill? true
                  :content :spark
                  :data [28 68 41 43 96 45 100]
                  :box-class "zbox-yellow"}]
        :histos [{:title "Page Views"
                  :gauge "12,938"
                  :summary "5%"
                  :summary-direction "up"
                  :content :histo
                  :data [28 68 41 43 96 45 100]
                  :box-class "zbox-green"}
                 {:title "Downloads"
                  :gauge "758"
                  :summary "1.3%"
                  :summary-direction "down"
                  :content :histo
                  :data [28 68 41 43 96 45 100]
                  :box-class "zbox-red"}
                 {:title "Sign-Ups"
                  :gauge "1,293"
                  :summary "6.75%"
                  :summary-direction "up"
                  :content :histo
                  :data [28 68 41 43 96 45 100]
                  :box-class "zbox-purple"}
                 {:title "Sales"
                  :gauge "8"
                  :summary "1.3%"
                  :summary-direction "down"
                  :content :histo
                  :data [28 68 41 43 96 45 100]
                  :box-class "zbox-yellow"}]}))

(defn make-box
  [config owner]
  (om/component
   (d/div #js {:className "col-sm-3"} (om/build box config))))

(defn main-view
  [app owner]
  (om/component
   (d/div #js {:className "container"}
          ;; Show simple gauges
          (d/div #js {:className "row"}
                 (d/h2 #js {:className "col-sm-12"} "Gauges"))
          (d/div #js {:className "row"}
                 (om/build-all make-box (:gauges app)))

          ;; Show our sparklines
          (d/div #js {:className "row"}
                 (d/h2 #js {:className "col-sm-12"} "Sparklines"))
          (d/div #js {:className "row"}
                 (om/build-all make-box (:sparklines app)))

          ;; Show our histograms
          (d/div #js {:className "row"}
                 (d/h2 #js {:className "col-sm-12"} "Histograms"))
          (d/div #js {:className "row"}
                 (om/build-all make-box (:histos app))))))

(om/root main-view app-state {:target (.getElementById js/document "app")})

;;
;; Push a new value somewhere every 500ms
;; Helps display transitions
;;
(let [push-val (fn [v] (fn [coll] (->> (conj coll v) (drop 1) (vec))))]
  (go
    (loop [_ (<! (timeout 500))]
      (let [i (rand-int 4)
            v (rand-int 101)]
        (swap! app-state update-in [:sparklines i :data] (push-val v))
        (swap! app-state update-in [:histos i :data] (push-val v))
        (recur (<! (timeout 500)))))))
