(ns pull-model
  (:require [babashka.process :as process]
            [clojure.core.async :as async]))

(try
  (let [llm (get (System/getenv) "LLM")
        url (get (System/getenv) "MODEL_REPO_BASE_URL")]
    (println (format "pulling model %s using %s" llm url))
    (if (and llm url (not (#{"gpt-4" "gpt-3.5"} llm)))

      ;; ----------------------------------------------------------------------

      ;; just call `model pull` here - create MODEL_HOST from MODEL_REPO_BASE_URL
      ;; ----------------------------------------------------------------------
      ;; TODO - this still doesn't show progress properly when run from docker compose

      (let [done (async/chan)]
        (async/go-loop [n 0]
          (let [[v _] (async/alts! [done (async/timeout 5000)])]
            (if (= :stop v) :stopped (do (println (format "... pulling model (%ss) - will take several minutes" (* n 10))) (recur (inc n))))))
        (process/shell {:env {"MODEL_HOST" url} :out :inherit :err :inherit} (format "./bin/ollama pull %s" llm))
        (async/>!! done :stop))

      (println "MODEL_REPO model only pulled if both LLM and MODEL_REPO_BASE_URL are set and the LLM model is not gpt")))
  (catch Throwable _ (System/exit 1)))
