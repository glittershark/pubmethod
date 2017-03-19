(ns glittershark.pubmethod.lib.extend-fn
  (:require [clojure.spec :as s]))

(do ;; specs {{{
    (s/def :protocol/method-impl
      (s/cat :method-name simple-symbol?
             :args :clojure.core.specs/arg-list
             :body (s/* any?)))
    (s/def :protocol/impl
      (s/* (s/cat :protocol-name symbol?
                  :method-impls (s/* (s/spec :protocol/method-impl)))))
    (s/def ::max-arg-count nat-int?))
;; }}}

(defn- build-fn-protocol-impls [fncall-head max-arg-count is-cljs]
  (let [invoke-sym (if is-cljs '-invoke 'invoke)]
    (for [n-args (range (inc max-arg-count))
          :let [argsyms (map (comp gensym (partial str "a")) (range n-args))]]
      `(~invoke-sym [this# ~@argsyms] (~@fncall-head this# ~@argsyms)))))

(defn- infer-max-arg-count [args]
  (assert (vector? args) "fndef args must be a vector")
  (if (some #{'&} args)
    (let [[fixed-args [_ & [rest-args]]] (split-with (complement #{'&}) args)]
      (if (vector? rest-args)
        (when-let [rest-count (infer-max-arg-count rest-args)]
          (+ (count fixed-args) rest-count))
        ; potentially unbounded args
        nil))
    (count args)))

(def ^:private infinite-max-args-count 20)

(defmacro extend-fn
  "Macro allowing reification of variadic IFns allong with other protocols,
   since normal protocol reification doesn't support var-args.

   fndef is the entriety of a (fully destructuring) function definition as in
   `fn`, without the leading `fn` keyword, and will be called with `this` as the
   first argument per normal protocol methods.

   protocol-specs is alternating protocol names and method implementations as in
   the normal arguments to `reify`.

   => (defprotocol Fooer (foo [this xs]))
   => (def x
        (extend-fn ([this & xs] (foo this xs))
          Fooer
          (foo [this xs] (map inc xs))))
   => (x 1 2 3)
   [2 3 4]"
  [fndef & protocol-specs]
  (let [is-cljs? (boolean (:ns &env))
        ifn (if is-cljs? 'IFn 'clojure.lang.IFn)
        fn-sym (gensym "fn")
        ;; dec for 'this
        max-arg-count (dec (or (infer-max-arg-count (first fndef))
                               (inc infinite-max-args-count)))]
    `(let [~fn-sym (fn ~fndef)]
       (reify
         ~ifn
         ~@(build-fn-protocol-impls (list fn-sym) max-arg-count is-cljs?)
         ~@protocol-specs))))

(comment
  "http://dev.clojure.org/jira/browse/CLJ-2002"
  ;; this spec is stale, too
  (s/fdef extend-fn :args (s/cat :opts (s/? (s/keys :opt-un [::max-arg-count]))
                                 :fncall-head (s/coll-of any?)
                                 :protocol-specs (s/* :protocol/impl))))
