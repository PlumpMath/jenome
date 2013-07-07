(ns jenome.test-core
  (:use [midje.sweet]
        [clojure.java.io :only [as-file resource]]
        [jenome.core]))


(facts "about utility functions"
       (map nybs-to-bases [0 1 2 3]) => [:T :C :A :G]
       (map nybs-to-bases [0 1 2 3]) => [:T :C :A :G]
       (byte-to-base-pairs 0X1B)     => [:T :C :A :G]
       (rounding-up-divide 4 5)      => 1
       (rounding-up-divide 5 5)      => 1
       (rounding-up-divide 6 5)      => 2
       (rounding-up-divide 10 5)     => 2
       (rounding-up-divide 11 5)     => 3)


(defn yeast-section [from to]
  (->> (resource "sacCer3.2bit") 
       as-file
       genome-sequence
       (drop from)
       (take (- to from 1))
       (map name)
       (apply str)))


(facts "about example yeast Genome file"
       (.exists (as-file (resource "sacCer3.2bit"))) => true
       (->> (resource "sacCer3.2bit")
            as-file
            genome-sequence
            (take 10)
            (map name)
            (apply str)) => "CCACACCACA"
       (yeast-section 0 11) => "CCACACCACA"
       (yeast-section 1 12) => "CACACCACAC"
       (yeast-section 10000 10011) => "GAATGAATCG"
       (yeast-section 100000 100011) => "GGTATTATTT")


(facts "about yeast metadata"
       (let [hdrs(->> (resource "sacCer3.2bit")
                      as-file
                      sequence-headers)]
         (count hdrs) => 17
         (monotonic? (map :dna-offset hdrs)) => true
         (map :n-block-sizes hdrs) => (repeat 17 ())
         (map :mask-block-sizes hdrs) => (repeat 17 ())))

(facts "about determining read blocks"
       (get-buffer-starts-and-lengths 0 1000 1000) => [[0 1000]]
       (get-buffer-starts-and-lengths 0 1000 1001) => [[0 1000] [1000 1]]
       (get-buffer-starts-and-lengths 100 200 512) => [[100 200] [300 200] [500 112]])
