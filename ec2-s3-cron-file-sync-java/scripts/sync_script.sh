#!/bin/bash
# Define absolute paths (update these paths as needed)
SYNC_FOLDER="/home/ubuntu/proj1/sync"       # e.g., /home/ubuntu/sync
S3_BUCKET="s3://uco-cicc-media-group6"  # Replace with your actual S3 bucket name
LOG_FILE="$SYNC_FOLDER/cron_log.dat"

# Append a starting sync message with timestamp to the cron log
echo "$(date '+%m-%d-%Y %H:%M:%S') - Starting S3 sync" >> $LOG_FILE

# Execute the AWS S3 sync command
aws s3 sync "$SYNC_FOLDER" "$S3_BUCKET" >> $LOG_FILE 2>&1

# Append a completion message with timestamp to the cron log
echo "$(date '+%m-%d-%Y %H:%M:%S') - S3 sync completed" >> $LOG_FILE
