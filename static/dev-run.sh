#!/bin/sh
export omny_db_type=storage
export storageSystem=local
export localFolder="."
export omny_static_location="public_html"
java -jar AllInOne-1.0.jar configure ./omny-config.json
