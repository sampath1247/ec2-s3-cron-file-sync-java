# Developer Notes

- Java program:
  - Generates `date_time.dat`, `mem_names.dat` with 1/3/5 minute randomized waits on independent timelines.
  - Logs actions to `log.dat`.
  - Write absolute paths to the `sync/` directory in code for EC2 (e.g., `/home/ubuntu/proj1/sync`).

- Bash sync:
  - `scripts/sync_script.sh` uses `aws s3 sync` and appends to `cron_log.dat`.
  - Use EC2 IAM role (no local credentials).
  - Keep the sync job short (seconds), so cron runs do not overlap.

- Cron:
  - Install with `crontab -e`.
  - Remove entry after demo to stop periodic syncs.

- Costs:
  - Keep EC2/S3 in same region; intra-region transfer is free.
  - Terminate EC2, delete bucket, remove IAM role/policy after demo.

- CI:
  - No AWS calls in CI. Only compile Java and lint shell.