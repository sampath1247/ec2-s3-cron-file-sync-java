# ec2-s3-cron-file-sync-java
Cloud file-sync demo: multithreaded Java writes timestamp/name files on EC2; cron runs aws s3 sync to an S3 bucket every 2 min (alt: every other day); IAM-secured; logs included.

# EC2 ↔ S3 File Synchronization Prototype (Java + Bash + Cron)

Course: Cyber Infrastructure & Cloud Computing  
Purpose: Prototype a file synchronization pipeline where an EC2 instance periodically syncs a local folder to an S3 bucket using an IAM role (no access keys in code), driven by cron.

## What this project demonstrates
- AWS setup:
  - EC2 instance + S3 bucket in the same region
  - IAM role attached to EC2 with S3 permissions (list, get, put, delete) scoped to your bucket
- Local file generation:
  - Java program writes to a `sync/` folder:
    - `date_time.dat`: appends timestamps at randomized intervals (1/3/5 minutes)
    - `mem_names.dat`: appends group member names at randomized intervals (independent timeline)
    - `log.dat`: records program start/actions/waits
- Synchronization:
  - Bash script invokes `aws s3 sync` to upload `sync/` to your S3 bucket and logs events to `cron_log.dat`
  - Cron job schedules the sync every 2 minutes (demo) or every other day (optional variant)
- Cost control:
  - Uses AWS Free Tier eligible services and IAM role best practices
  - Teardown instructions included

## Repository layout
```
.
├─ java/
│  └─ FileSyncLogger.java            # Java program generating files in sync/
├─ scripts/
│  └─ sync_script.sh                 # aws s3 sync + timestamp logging
├─ sync/                             # generated files during runs (gitignored)
├─ docs/
│  ├─ Cyber Infrastruct &Cloud Comp proj1.txt
│  ├─ Cyber Infrastruct &Cloud Comp proj1 report.txt
│  └─ Report.docx                    # optional
├─ cronjobschedule.txt               # crontab lines (every 2 mins; every other day variant)
├─ .github/workflows/ci.yml          # compile Java + bash lint
├─ .gitignore
├─ LICENSE
└─ README.md
```

## Prerequisites
- On EC2 (Ubuntu recommended):
  - OpenJDK 17+ (or 11+): `sudo apt-get update && sudo apt-get install -y openjdk-17-jdk`
  - AWS CLI v2: https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html
  - An S3 bucket in the same region as your EC2
  - An IAM role attached to the EC2 instance with a policy scoped to your bucket (list/get/put/delete)

Example minimal S3 policy (replace BUCKET_NAME and ACCOUNT_ID):
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "SyncAccess",
      "Effect": "Allow",
      "Action": [
        "s3:ListBucket"
      ],
      "Resource": "arn:aws:s3:::BUCKET_NAME"
    },
    {
      "Sid": "ObjectAccess",
      "Effect": "Allow",
      "Action": [
        "s3:GetObject", "s3:PutObject", "s3:DeleteObject"
      ],
      "Resource": "arn:aws:s3:::BUCKET_NAME/*"
    }
  ]
}
```

Attach this role to your EC2: EC2 → Instances → Actions → Security → Modify IAM role.

## Build and run the Java generator
```bash
# from repo root on EC2
mkdir -p sync
javac java/FileSyncLogger.java
# Runs in foreground; path inside the code should point to the absolute sync dir, e.g., /home/ubuntu/proj1/sync
java -cp java FileSyncLogger
```
- Verify files appear in `sync/`: `date_time.dat`, `mem_names.dat`, `log.dat`.

## Configure and test the sync script
Edit `scripts/sync_script.sh`:
- Set `SYNC_DIR` to your absolute sync folder (e.g., `/home/ubuntu/proj1/sync`)
- Set `BUCKET` to `s3://your-bucket-name`

Manual test:
```bash
bash scripts/sync_script.sh
# Check S3 for uploaded files and sync/cron_log.dat for a new entry
```

## Schedule with cron
Install your cron entry:
```bash
crontab -e
# Paste the line from cronjobschedule.txt (every 2 minutes)
```

- Every 2 minutes (demo):
```
*/2 * * * * /home/ubuntu/proj1/scripts/sync_script.sh >> /home/ubuntu/proj1/sync/cron_log.dat 2>&1
```
- Every other day at midnight:
```
0 0 */2 * * /home/ubuntu/proj1/scripts/sync_script.sh >> /home/ubuntu/proj1/sync/cron_log.dat 2>&1
```

Stop the cron job after your demo:
```bash
crontab -e   # remove the line or comment it out
```

## Free tier and teardown
- EC2 t2.micro/t3.micro free for eligible accounts (750 hrs/month for 12 months)
- S3: 5 GB free-tier storage; intra-region EC2→S3 transfers are free
- IAM: free
Teardown after demo:
- Terminate EC2 instance
- Empty and delete the S3 bucket
- Detach/delete project IAM role/policy
- Set up AWS Budgets alerts; verify zero usage after teardown

## Security notes
- Do NOT hardcode AWS keys in code or repo. Use the EC2 IAM role.
- Keep bucket names generic if you plan to publish logs/screen captures.
- All `.dat` and logs are generated—kept out of version control.

## CI
- GitHub Actions compiles the Java file (if present) and lints the shell script.

## License
MIT (see LICENSE)
