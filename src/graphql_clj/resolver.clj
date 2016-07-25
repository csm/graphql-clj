(ns graphql-clj.resolver
  (:require [clojure.core.match :as match]))

(defn default-resolver-fn [type-name field-name]
  (fn [context parent & args]
    (println (format "Not found resolver function for type(%s) and field(%s)." type-name field-name))
    (get parent (keyword field-name))))

(defn schema-introspection-resolver-fn
  [schema]
  (fn [type-name field-name]
    (match/match
     [type-name field-name]
     ["QueryRoot" "__schema"] (fn [context parent & args]
                                {:types {}
                                 :queryType nil
                                 :mutationType nil
                                 :directives nil})
     :else nil)))

(defn create-resolver-fn
  [schema resolver-fn]
  (let [schema-resolver-fn (schema-introspection-resolver-fn schema)]
    (fn [type-name field-name]
      (or (and resolver-fn (resolver-fn type-name field-name))
          (schema-resolver-fn type-name field-name)
          (default-resolver-fn type-name field-name)))))
