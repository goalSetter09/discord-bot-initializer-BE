#!/bin/bash

# Directory of the script
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

# Install requirements
python3 -m pip install -U -r "$DIR/requirements.txt"

# Make the script executable
chmod +x "$DIR/install_requirements.sh"