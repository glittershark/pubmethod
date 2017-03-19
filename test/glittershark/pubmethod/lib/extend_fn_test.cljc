(ns glittershark.pubmethod.lib.extend-fn-test
  (:require #?(:clj  [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros [deftest is testing]])
            #?(:clj [glittershark.pubmethod.lib.extend-fn :refer [extend-fn]]))
  #?(:cljs (:require-macros
             [glittershark.pubmethod.lib.extend-fn :refer [extend-fn]])))

(defprotocol TestProto (foo [_]))

(deftest extend-fn-with-lambda-test
  (let [subject (extend-fn
                  ([_ arg] (str "result" arg))

                  TestProto
                  (foo [_] :foo))]

    (is (= "result1" (subject 1))
        "Acts like a fn, calling the lambda")

    (is (= :foo (foo subject))
        "Implements all other passed protocols")))

(deftest extend-fn-with-fn-head-test
  (let [subject (extend-fn
                  ([this] (foo this))
                  TestProto
                  (foo [_] :foo))]

    (is (= :foo (subject))
        "Acts like a fn, putting the fn head at the front of the call")

    (is (= :foo (foo subject))
        "Implements all other passed protocols")))
