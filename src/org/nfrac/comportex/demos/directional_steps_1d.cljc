(ns org.nfrac.comportex.demos.directional-steps-1d
  (:require [org.nfrac.comportex.core :as core]
            [org.nfrac.comportex.encoders :as enc]
            [org.nfrac.comportex.util :as util]))

(def bit-width 300)
(def cat-bit-width 60)
(def numb-bit-width (- bit-width cat-bit-width))
(def numb-max 7)
(def numb-domain [0 numb-max])
(def on-bits 30)

(def spec
  {:column-dimensions [500]
   :ff-potential-radius 0.2
   :ff-stimulus-threshold 3
   :global-inhibition? false
   :activation-level 0.04
   :boost-active-every 10000
   :depth 4
   })

(def higher-level-spec-diff
  {:column-dimensions [500]
   :ff-max-segments 5})

(def initial-input-val [:up 0])

(defn input-transform
  [[dir i]]
  (let [new-i (-> (case dir
                    :up (inc i)
                    :down (dec i))
                  (min numb-max)
                  (max 0))
        new-dir (util/rand-nth [:up :down])]
    [new-dir new-i]))

(def encoder
  (enc/encat 2
             (enc/category-encoder cat-bit-width [:down :up])
             (enc/linear-encoder numb-bit-width on-bits numb-domain)))

(defn world-seq
  "Returns an infinite lazy seq of sensory input values."
  []
  (iterate input-transform initial-input-val))

(defn n-region-model
  ([n]
     (n-region-model n spec))
  ([n spec]
     (core/regions-in-series core/sensory-region (core/sensory-input encoder)
                             n
                             (list* spec (repeat (merge spec higher-level-spec-diff))))))
