#syntax = docker/dockerfile:1.4

FROM ollama/ollama:latest AS model 
# FROM huggingface/huggingface:latest AS model
FROM babashka/babashka:latest

# just using as a client - never as a server
# HME-2023-10-17: this needs a fix to model
COPY --from=model /bin/ollama ./bin/ollama

COPY pull_model.clj pull_model.clj

ENTRYPOINT ["bb", "-f", "pull_model.clj"]

