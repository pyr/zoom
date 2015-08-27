(ns zoom.spline
  "Functions to compute splines between points.")


(defn spline-curve
  "http://scaledinnovation.com/analytics/splines/aboutSplines.html"
  [t w h]
  (fn [[[x0 y0] [x1 y1] [x2 y2]]]
    (let [sqrt    (fn [x] (.sqrt js/Math x))
          sqrd    (fn [x] (.pow js/Math x 2))
          d01     (sqrt (+ (sqrd (- x1 x0)) (sqrd (- y1 y0))))
          d12     (sqrt (+ (sqrd (- x2 x1)) (sqrd (- y2 y1))))
          fa      (/ (* t d01) (+ d01 d12))
          fb      (/ (* t d12) (+ d01 d12))
          p1x     (- x1 (* fa (- x2 x0)))
          p1y     (- y1 (* fa (- y2 y0)))
          p2x     (+ x1 (* fb (- x2 x0)))
          p2y     (+ y1 (* fb (- y2 y0)))]
      [p1x p1y p2x p2y])))
