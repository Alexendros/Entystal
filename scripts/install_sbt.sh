#!/usr/bin/env bash
# Instalador simple de sbt para Debian/Ubuntu
set -e

sudo apt-get update
sudo apt-get install -y curl gnupg

curl -sL https://repo.scala-sbt.org/scalasbt/repo.gpg | \
  sudo gpg --dearmor -o /usr/share/keyrings/sbt-release.gpg

echo "deb [signed-by=/usr/share/keyrings/sbt-release.gpg] https://repo.scala-sbt.org/scalasbt/debian all main" | \
  sudo tee /etc/apt/sources.list.d/sbt.list

echo "deb [signed-by=/usr/share/keyrings/sbt-release.gpg] https://repo.scala-sbt.org/scalasbt/debian /" | \
  sudo tee /etc/apt/sources.list.d/sbt_old.list

sudo apt-get update
sudo apt-get install -y sbt
