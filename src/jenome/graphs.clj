(ns jenome.graphs
  (:import [org.jfree.data.xy XYSeriesCollection XYSeries]
           [org.jfree.chart ChartFrame JFreeChart]
           [org.jfree.chart.plot XYPlot]
           [org.jfree.chart.axis NumberAxis]
           [org.jfree.chart.renderer.xy XYBarRenderer StandardXYBarPainter]
           [org.jfree.chart.renderer.category]))


(defn get-lengths
  "
  Convert any seq to a sequence of integers representing number of
  duplicates in the original seq. 
  "
  [s]
  (->> s
       (partition-by identity)
       (remove #(= (first %) :N))
       (map count)))


(defn trim-zeros 
  "
  Convert zeros (or negatives) to small positive values to allow for
  graphing on log scale
  "
  [vals]
  (map (fn [[x y]] [x (if (> y 0) y 0.0001)]) vals))


(defn make-hist
  "
  Convert seq of input xs into a histogram of nbins bins, from xmin to
  xmax.  Discard overflows or underflows
  "
  [xmin xmax nbins xs]
  (let [;; "base" histogram (zeros):
        zero-map (into (sorted-map)
                       (map (fn [x] [x 0]) (range nbins)))
        ;; actual bin values for every input in xs:
        xbins (map #(int (* nbins (/ (- % xmin)
                                     (- xmax xmin))))
                   xs)
        ;; strip out undeflows & overflows:
        no-overflows (->> xbins
                          (remove #(< % 0))
                          (remove #(>= % nbins)))]
    ;; yield histogram as array of [ibin, height] pairs:
    (into [] (reduce #(update-in %1 [%2] inc) zero-map no-overflows))))


(defn draw-hist
  "
  Draw histogram of bins as generated by make-hist
  "
  [x-label values]
  (let [renderer (XYBarRenderer.)
        painter (StandardXYBarPainter.)
        series (XYSeries. [])
        blue (java.awt.Color. 0x3b 0x6c 0x9d)
        coll (XYSeriesCollection. series)
        y-axis (org.jfree.chart.axis.LogarithmicAxis. "Entries")
        plot (XYPlot. coll (NumberAxis. x-label) y-axis renderer)
        panel (JFreeChart. plot)
        frame (ChartFrame. "Histogram" panel)]
    (doto plot
      (.setBackgroundAlpha 0.0)
      (.setRangeGridlinesVisible false)
      (.setDomainGridlinesVisible false))
    (doto renderer
      (.setBarPainter painter)
      (.setPaint blue)
      (.setDrawBarOutline true)
      (.setOutlinePaint blue)
      (.setOutlineStroke (java.awt.BasicStroke. 1))
      (.setShadowVisible false))
    (doseq [[x y] values]
      (.add series (+ x 0.5) y))
    (.setLowerBound y-axis 0.5)
    (.setVisible (.getLegend panel) false)
    (doto frame
      (.setSize 800 250)
      (.setVisible true))))
