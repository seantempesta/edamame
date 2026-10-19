;;   Copyright (c) Nicola Mometto, Rich Hickey & contributors.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns ^:no-doc edamame.impl.reader.utils
  (:refer-clojure :exclude [char]))

(defn char [x]
  (when x
    (clojure.core/char x)))

(defn ex-info? [ex]
  (instance? clojure.lang.ExceptionInfo ex))

(defn whitespace?
  "Checks whether a given character is whitespace"
  [ch]
  (when ch
    (or (Character/isWhitespace ^Character ch)
        (identical? \,  ch))))

(defn numeric?
  "Checks whether a given character is numeric"
  [^Character ch]
  (when ch
    (Character/isDigit ch)))

(defn newline?
  "Checks whether the character is a newline"
  [c]
  (or (identical? \newline c)
      (nil? c)))

(defn desugar-meta
  "Resolves syntactical sugar in metadata" ;; could be combined with some other desugar?
  [f]
  (cond
    (keyword? f) {f true}
    (symbol? f)  {:tag f}
    (string? f)  {:tag f}
    (vector? f)  {:param-tags f}
    :else        f))

(defn make-var
  "Returns an anonymous unbound Var"
  []
  (with-local-vars [x nil] x))

(defn namespace-keys [ns keys]
  (for [key keys]
    (if (or (symbol? key)
            (keyword? key))
      (let [[key-ns key-name] ((juxt namespace name) key)
            ->key (if (symbol? key) symbol keyword)]
        (cond
          (nil? key-ns)
          (->key ns key-name)

          (= "_" key-ns)
          (->key key-name)

          :else
          key))
      key)))

(defn second' [[a b]]
  (when-not a b))
