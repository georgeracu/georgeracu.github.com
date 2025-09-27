ARG VARIANT=2.7-bullseye
# ARG VARIANT=3.3-bookworm

FROM mcr.microsoft.com/devcontainers/jekyll:${VARIANT}

# Install build dependencies
RUN apt-get update && apt-get install -y \
    build-essential \
    git \
    rbenv \
    && rm -rf /var/lib/apt/lists/*

# Install Jekyll and Bundler
RUN gem install bundler

# Install only runtime dependencies
RUN apt-get update && apt-get install -y \
    git \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

# Set environment
ENV JEKYLL_ENV=development
ENV BUNDLE_PATH=/usr/local/bundle

CMD ["sleep", "infinity"]
