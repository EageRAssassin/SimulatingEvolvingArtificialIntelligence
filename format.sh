#!/usr/bin/env bash

if ! [[ -x $(command -v clang-format) ]]; then
    echo "This script requires 'clang-format' to execute...please install it and try again." >&2
    exit 1
fi

find . -name *.java | xargs clang-format -i -style='{BasedOnStyle: google, IndentWidth: 4, ColumnLimit: 100}'
