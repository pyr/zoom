(ns zoom.box
  (:require [om.core     :as om :include-macros true]
            [om.dom      :as d  :include-macros true]
            [zoom.spark  :refer [spark]]
            [zoom.histo  :refer [histo]]))

(def default-fill       "rgba(255, 255, 255, 0.5)")
(def default-stroke     "#fff")
(def default-line-width "2")

(def known-content? #{:spark :histo})

(defn box
  [{:keys [content box-class gauge title summary summary-direction] :as app} owner]
  (om/component
   (d/div #js {:className (str "zbox " box-class)}
          (d/div #js {:className "zbox-header"}
                 (d/span #js {:className "zbox-title"} title)
                 (d/h2 #js {:className "zbox-gauge"}
                       (str gauge " ")
                       (d/small #js {:className (str "zbox-gauge-summary "
                                                     "zbox-gauge-summary-"
                                                     summary-direction)}
                                summary)))
          (when (known-content? content)
            (d/div
             nil
             (d/hr #js {:className "zbox-divider"})
             (case content
               :spark (om/build spark app)
               :histo (om/build histo app)))))))
