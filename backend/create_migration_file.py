import os
import sys
from datetime import datetime


def main():
  if len(sys.argv) != 2:
    print("Usage: python create_migration_file.py <migration_description>")
    sys.exit(1)

  # Get the migration description from the command line argument
  description = sys.argv[1]

  # Generate timestamp
  timestamp = datetime.now().strftime("%Y%m%d%H%M%S")

  # Create the filename
  filename = f"V{timestamp}__{description.replace(' ', '_')}.sql"

  # Ensure the migrations directory exists
  os.makedirs(".", exist_ok=True)

  # Create the migration file
  filepath = os.path.join(".", "src", "main", "resources", "db", "migration",
                          filename)
  with open(filepath, 'w') as file:
    file.write("-- Migration script - {}\n".format(filename))

  print(f"Created migration file: {filepath}")


if __name__ == "__main__":
  main()
