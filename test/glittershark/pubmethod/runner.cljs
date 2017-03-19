(ns glittershark.pubmethod.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [glittershark.pubmethod-test]
            [glittershark.pubmethod.lib.extend-fn-test]))

(doo-tests 'glittershark.pubmethod-test
           'glittershark.pubmethod.lib.extend-fn-test)
