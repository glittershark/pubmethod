# Pubmethod

Synchronous static publish/subscribe in Clojure(script)

## Rationale

In my experience, I've encountered two primary use-cases for Clojure's
[multimethods][]:

1. The same conceptual action taken on different "types" of argument, ie
   conversion to a type, pretty-printing, etc. This is analogous to traditional
   OOP interfaces, but superior because implementations can be defined in a
   different place than the type, and the method used for determining dispatch
   can be customized.
2. An event occurring, and various places in the code wanting to perform some
   sort of impure (side-effectful, we don't care about the return value) action
   in response to that event. The first time I ran into this was handlers for
   [sente][] channel events, but it happens all over the place

Multimethods are really good at the first one - it's what they were designed
for, and it's the way clojure.core and other core libraries use them all the
time. However, they fall short on the second use-case, primarily because they
lock you in to defining one and exactly one handler for a given dispatch value.

This library is intended to be a drop-in replacement for Clojure multimethods,
with support for defining more than one handler per dispatch value.

[multimethods]: https://clojure.org/reference/multimethods
[sente]: https://github.com/ptaoussanis/sente

## Usage

If you haven't already, read the [documentation for multimethods][mm-doc]. This
section assumes at least a passing understanding of how multimethods work.

[mm-doc]: https://clojure.org/reference/multimethods

First, require the namespace

```clojure
(require '[glittershark.pubmethod :refer [defpub defsub]])
```

`defpub` is analogous to `defmulti`, and `defsub` is analogous to `defmethod`.
In the default case, the arguments are the same and they work the same:

```clojure
(defpub foo :id)
(defsub foo :a [_] :a-called!)
(defsub foo :b [{:keys [some-arg]}] some-arg)
(defsub foo :default [_] :default-handler)

(foo {:id :a}) ;; => :a-called!
(foo {:id :b, :some-arg :foobar}) ;; => :foobar
(foo {:id :unknown}) ;; => :default-handler
```

Just like with `defmethod`, registering a handler for the same dispatch value
twice will overwrite the first:

```clojure
(defsub foo :a [_] :overwritten!)
(foo {:id :a}) ;; => :overwritten!    
```

If you want to define auxiliary handlers for the same dispatch value, each
distinct handler has to be given a unique key:

```clojure
(defsub foo :b :my-aux-handler [_] 
  (println "hello from auxiliary handler!"))

(foo {:id :b, :some-arg 1}) 
;; prints "hello from auxiliary handler"
;; returns 1
```

Just like with the primary handler, registering the same auxiliary handler twice
will overwrite the first. Also notice that the return value is the return value
of the primary handler.

If a dispatch value has *only* auxiliary handlers and no primary, the return
value of a call to the pubmethod will be `nil`.

## Caveat

Pubmethod currently provides no guarantee as to the order that handlers are
executed, so you should avoid writing code that relies on order. If this is a
deal-breaker for you, please feel free to report an issue.

## License

Copyright © 2017 Griffin Smith. Distributed under the MIT License
