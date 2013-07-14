(ns jenome.examples
  (:use jenome.core
        [clojure.java.io :only [resource as-file]]))


; Adjust location to suit:
(def human "/Users/jacobsen/Programming/Lisp/Clojure/jenome/hg19.2bit")
(def yeast (as-file (resource "sacCer3.2bit")))


(defmacro tib
  "
  tib: Time in the Background
  Run body in background, printing body and showing result when it's done.
  "
  [& body]
  `(future (let [code# '(~@body)
                 junk# (println "Starting" code#)
                 result# (time ~@body)]
             (println code# "\n" result#))))


(defn count'
  "
  Non-overflowing version of count
  "
  [s]
  (loop [s s, n 0] (if (seq s)
                     (recur (rest s)
                            (inc' n))
                     n)))


(defn write-seq 
  "
  Write a (potentially very long) sequence of lines to a text file
  "
  [filename s]
  (with-open [wrt (clojure.java.io/writer filename)]
    (doseq [x s]
      (.write wrt (str x "\n")))))


(defn pseq
  "
  Apply func in parallel to all sequences specified in the index of fname.
  "
  [fname func]
  (let [hdrs (sequence-headers fname)
        map-fn (fn [hdr] (->> (genome-sequence fname hdr)
                             func))]
    (pmap map-fn hdrs)))


(comment

  (monotonic? (map :dna-offset (sequence-headers human))) ;;=> true

  ;; Get the names of the sequences (mostly chromosomes)
  (map :name (sequence-headers yeast))

  ;; Get the first 100 base pairs of yeast:
  (->> yeast
       genome-sequence
       (take 100)
       genome-str)  

  ;; How many base pairs?
  (->> yeast
       genome-sequence
       count)
  
  (tib (apply + (pseq yeast count)))
  (tib (apply + (pseq yeast count')))

  
  ;; How many base pairs?
  (with-out-str
    (time
     (->> human
          genome-sequence
          (take (* 1000 1000 1))
          count)))

  
  ;; Relative frequencies of base pairs:
  (->> yeast
       genome-sequence
       frequencies)

  ;; In case we need it, here's an infinite random genome:
  (defn randgenome []
    (repeatedly #(rand-nth [:A :G :C :T])))
  
  (time (->> yeast
             genome-sequence
             count))
  
  ;; Use pmap and partition-all instead -- uses about 360% of my
  ;; 4-core MBP, same as count by itself
  (->> human
       genome-sequence
       (partition-all 100000)
       (pmap count)
       (apply +))

  (-> yeast
      genome-sequence
      count')
  
  (->> human
       genome-sequence
       (drop 10000)
       (take 100)
       (map name)
       (apply str))
  

  (clojure.pprint/pprint
   (let [seqcnt (file-header yeast)]
     (file-index yeast seqcnt)))

  
  (.mkdir (clojure.java.io/file "/tmp/decoded"))

  (tib
   (let [h2 (second (sequence-headers human))
         {:keys [name dna-offset dna-size]} h2]
     (let [fname (str "/tmp/decoded/" name ".fa")]
       (write-seq fname
                  (cons (str ">" name)
                        (->> (genome-sequence human h2)
                             (partition-all 50)
                             (map genome-str)))))))


  (->>
   human
   genome-sequence
   (take 100)
   frequencies)

  (->> "chr1.fa"
       (str "/Users/jacobsen/Programming/Lisp/Clojure/jenome/")
       clojure.java.io/reader
       line-seq
       (drop 1)
       (apply concat)
       (map str)
       (map #(.toUpperCase %))
       (take 10000)
       frequencies)

  (sequence-headers yeast)
  (sequence-headers human)
  (file-header human)
  (file-header yeast)

  ;; Get all "blank" spots
  (->> human
       sequence-headers
       first
       ((juxt :n-block-starts :n-block-sizes))
       (apply interleave)
       (partition 2)
       (map vec))

  (->>
   human
   genome-sequence
   (map-indexed vector)
   (take 100))

  (->>
   human
   genome-sequence
   (map name)
   (drop 10000)
   (take 1000)
   (apply str))

  (->>
   human
   genome-sequence
   (take 1000)
   (partition-by identity)
   (map (partial map name))
   (map (partial apply str)))
  
  (split-with identity (ran))
  ;; Approach for proceeding:
  ;; require genome-sequence to take an argument which is the entire
  ;; header structure.  have it use that to find the Ns.

  (float (/ 3137161264 12157105))

  (->> human
       sequence-headers
       (map (juxt :name :dna-size :n-block-count))
       clojure.pprint/pprint)

  (->> human
       sequence-headers
       first
       ((juxt :n-block-starts :n-block-sizes))
       (apply interleave)
       (partition 2)
       (map vec)
       clojure.pprint/pprint)

  (->> (range (* 1000 1000 1000 3))
       (partition (* 1000 1000))
       (pmap count)
       (apply +))

  (->> yeast
       genome-sequence
       frequencies)

  


  (->> human
       genome-sequence
       (drop 10000)
       (take 100))

  (tib (->> yeast
            genome-sequence
            (partition-by identity)
            (map count)
            frequencies
            (into (sorted-map))
            clojure.pprint/pprint))

  (tib (->> human
            genome-sequence
            (take (* 1000 1000 10))
            (remove #{:N})
            (partition-by identity)
            (map count)
            frequencies
            (into (sorted-map))
            clojure.pprint/pprint))

  (tib
   (let [fname human
         h2 (second (sequence-headers fname))
         {:keys [name dna-offset dna-size]} h2]
     (->> (genome-sequence fname h2)
          (take 40000)
          frequencies)))


  ;; Get frequencies for all sequences in parallel
  (tib
   (let [fname human
         hdrs (sequence-headers fname)
         freq-fn (fn [hdr] (->> (genome-sequence fname hdr)
                               (take 100000)
                               frequencies))]
     (apply (partial merge-with +) (pmap freq-fn hdrs))))

  

  )