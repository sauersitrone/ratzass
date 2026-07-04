# default target for make file without argument
default: up
# perform a powershell command and store the result in the variable 
current_date:= $(shell powershell -noprofile Get-date -Format yyyy-MM-dd-hh_mm_ss)
source_dir:= var/lib/mysql/initial-data-dump-$(current_date)

# retrive a global variable without need to perform a command
ENVIRONMENT := $(OS)

terry:
	@echo environment: $(ENVIRONMENT)
	@echo date: $(current_date)
	@echo xxx: $(source_dir) 

backup:
	docker compose exec -e current_date=$(current_date) mariadb bash -c "mariadb-dump -uroot -pzitrone zitrone > /var/lib/mysql/backup-$(current_date).sql"
	docker compose cp mariadb:/var/lib/mysql/backup-$(current_date).sql ./backups

initial-data:
	docker compose exec -e current_date=$(current_date) mariadb bash -c "mariadb-dump -uroot -pzitrone --extended-insert=FALSE --skip-comments --no-create-db --no-create-info --complete-insert --skip-disable-keys sitrone > /var/lib/mysql/initial-data-$(current_date).sql" 
	docker compose cp mariadb:/var/lib/mysql/initial-data-$(current_date).sql ./backups
#	powershell -noprofile Move-Item database/initial-data-$(current_date).sql backups/initial-data-$(current_date).sql

initial-data-dump:
	docker compose exec -e current_date=$(current_date) mariadb bash -c "mkdir $(source_dir); chmod 1777 $(source_dir); mysqldump -uroot -proot --skip-comments --no-create-db --no-create-info --skip-disable-keys --tab=$(source_dir) sitrone" 
	powershell -noprofile Move-Item database/initial-data-dump-$(current_date) backups/initial-data-dump-$(current_date)

up:
	docker-compose up -d --remove-orphans

build:
	docker-compose up -d --build --remove-orphans

local-build:
	docker-compose up -f docker-compose.local-predeploy.yml -d --build --remove-orphans

prune:
	docker-compose down

logs:
	docker-compose logs -f

# https://stackoverflow.com/questions/69832947/makefile-store-powershell-output-in-a-variable-for-futher-use
# https://stackoverflow.com/questions/51697218/how-to-execute-powershell-cmd-commands-using-gnuwin32-makefiles