(ns zoom.histo
  (:require [om.core     :as om :include-macros true]
            [om.dom      :as d  :include-macros true]))

(def default-fill       "rgba(255, 255, 255, 0.5)")
(def default-stroke     "#fff")
(def default-line-width "2")
(def default-padding    10)
(def default-spacing    5)

(defprotocol Paintable
  (paint! [this clear?]))

(defn rect-maker
  [step scale height pad sp]
  (fn [[index amount]]
    [(+ pad sp (* index step)) (- height (* amount scale))
     (- step (* sp 2))         (* amount scale)]))

(defn histo
  [{:keys [data padding spacing line-width fill-style stroke-style]}
   owner]
  (reify
    Paintable
    (paint! [this clear?]
      (let [node   (om/get-node owner)
            pad    (or padding default-padding)
            sp     (or spacing default-spacing)
            fullw  (.-width node)
            w      (-  (.-width node) (* pad 2))
            h      (.-height node)
            ctx    (.getContext node "2d")
            step   (/ w (count data))
            scale  (/ h 100)
            rect   (rect-maker step scale h pad sp)
            rects  (mapv rect (map-indexed vector data))]

        (when clear?
          (.clearRect ctx 0 0 fullw h))

        (.save ctx)
        (set! (.-strokeStyle ctx) (or stroke-style default-stroke))
        (set! (.-lineWidth ctx) (or line-width default-line-width))
        (set! (.-fillStyle ctx) (or fill-style default-fill))

        (doseq [[x y w h] rects]
          (.strokeRect ctx x y w h)
          (.fillRect ctx x y w h))
        (.restore ctx)))
    om/IDidMount
    (did-mount [this]
      (paint! this false))
    om/IDidUpdate
    (did-update [this _ _]
      (paint! this true))
    om/IRender
    (render [this]
      (d/canvas nil))))
