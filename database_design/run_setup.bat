@echo off
echo Running Sale & Payment Setup Script...
mysql --user=root --password=123456 --database=WarehouseRentalDB < "%~dp0sale_payment_setup.sql"
if %ERRORLEVEL% == 0 (
    echo SUCCESS: Triggers, Views, Stored Procedures created.
) else (
    echo ERROR: Check MySQL connection or SQL syntax.
)
pause

