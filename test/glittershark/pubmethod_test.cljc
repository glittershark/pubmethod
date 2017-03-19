(ns glittershark.pubmethod-test
  (:require #?(:clj  [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros [deftest is testing]])
            [glittershark.pubmethod
             #?(:clj :refer :cljs :refer-macros) [defpub defsub]]))

(defpub test-defpub :id)

(defsub test-defpub :a [_] 1)

(def aux-called (atom false))
(defsub test-defpub :b [_] 2)
(defsub test-defpub :b :aux-1 [_] (reset! aux-called true))

(defsub test-defpub :default [_] :default!)

;;;

(deftest primary-only-test
  (is (= 1 (test-defpub {:id :a}))))

(deftest aux-test
  (reset! aux-called false)
  (is (= 2 (test-defpub {:id :b})))
  (is @aux-called))

(deftest default-test
  (is (= :default! (test-defpub {:id :nope}))))
