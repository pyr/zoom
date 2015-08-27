(ns zoom.spark
  (:require [om.core     :as om :include-macros true]
            [om.dom      :as d  :include-macros true]
            [zoom.spline :refer [spline-curve]]))

(def default-fill       "rgba(255, 255, 255, 0.5)")
(def default-stroke     "#fff")
(def default-line-width "2")

(defprotocol Paintable
  (paint! [this clear?]))

(defn point-maker
  [step scale height]
  (fn [[index amount]]
    [(* index step) (- height (* amount scale))]))

(defn spark
  [{:keys [data fill? bezier? curve-tension
           line-width fill-style stroke-style]}
   owner]
  (reify
    Paintable
    (paint! [this clear?]
      (let [node   (om/get-node owner)
            w      (.-width node)
            h      (.-height node)
            ctx    (.getContext node "2d")
            step   (/ w (dec (count data)))
            scale  (/ h 100)
            point  (point-maker step scale h)
            tuples (map-indexed vector data)]

        (when clear?
          (.clearRect ctx 0 0 w h))
        (.save ctx)
        (.beginPath ctx)

        (set! (.-strokeStyle ctx) (or stroke-style default-stroke))
        (set! (.-lineWidth ctx) (or line-width default-line-width))

        (if bezier?

          ;; We were instructed to build a bezier type
          ;; curve between points.
          (let [points (partition 3 1 (-> (map point tuples)
                                          (conj nil)
                                          vec
                                          (conj nil)))
                xform  (juxt second (spline-curve curve-tension w h))]

            (doseq [[[p0 s0] [p1 s1]] (partition 2 1 (map xform points))]
              (let [[x0 y0] p0]
                (when (zero? x0)
                  (.moveTo ctx x0 y0))
                (when-let [[x1 y1] p1]
                  (let [[_ _ ox oy] s0
                        [ix iy _ _] s1]
                    (.bezierCurveTo ctx ox oy ix iy x1 y1))))))
          (doseq [[x y] (map point tuples)]
            (if (= 0 x)
              (.moveTo ctx x y)
              (.lineTo ctx x y))))
        (.stroke ctx)

        (when fill?
          (let [[x1 y1] (point [(dec (count data)) 0])
                [x2 y2] (point [0 0])]
            (.lineTo ctx x1 y1)
            (.lineTo ctx x2 y2)
            (set! (.-fillStyle ctx) (or fill-style default-fill))
            (.closePath ctx)
            (.fill ctx)))
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
