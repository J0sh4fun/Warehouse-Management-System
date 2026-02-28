# ============================================
# MODULE 2 - AUTOMATED TEST SCRIPT (PowerShell)
# ============================================
# Run: powershell -ExecutionPolicy Bypass -File MODULE_2_TEST.ps1

$BASE_URL = "http://localhost:8080"
$ADMIN_EMAIL = "root@example.com"
$ADMIN_PASS = "hung1342005"
$DEFAULT_KHO_ID = 1
$DEFAULT_KHACH_HANG_ID = 1

$totalTests = 0
$passedTests = 0
$failedTests = 0

function Add-Result {
    param(
        [string]$Name,
        [bool]$Passed,
        [string]$Detail = ""
    )

    $script:totalTests++

    if ($Passed) {
        $script:passedTests++
        Write-Host "[PASS] $Name" -ForegroundColor Green
    } else {
        $script:failedTests++
        if ([string]::IsNullOrWhiteSpace($Detail)) {
            Write-Host "[FAIL] $Name" -ForegroundColor Red
        } else {
            Write-Host "[FAIL] $Name :: $Detail" -ForegroundColor Red
        }
    }
}

function Print-Header {
    param([string]$Title)

    Write-Host ""
    Write-Host "=====================================" -ForegroundColor Cyan
    Write-Host $Title -ForegroundColor Cyan
    Write-Host "=====================================" -ForegroundColor Cyan
}

function Try-ParseJson {
    param([string]$Raw)

    if ([string]::IsNullOrWhiteSpace($Raw)) {
        return $null
    }

    try {
        return $Raw | ConvertFrom-Json
    } catch {
        return $Raw
    }
}

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Endpoint,
        $Body = $null,
        [string]$Token = ""
    )

    $uri = "$BASE_URL$Endpoint"
    $headers = @{}

    if (-not [string]::IsNullOrWhiteSpace($Token)) {
        $headers["Authorization"] = "Bearer $Token"
    }

    $jsonBody = $null
    if ($null -ne $Body) {
        $headers["Content-Type"] = "application/json"
        $jsonBody = $Body | ConvertTo-Json -Depth 10
    }

    try {
        if ($null -ne $jsonBody) {
            $resp = Invoke-WebRequest -Uri $uri -Method $Method -Headers $headers -Body $jsonBody -UseBasicParsing -ErrorAction Stop
        } else {
            $resp = Invoke-WebRequest -Uri $uri -Method $Method -Headers $headers -UseBasicParsing -ErrorAction Stop
        }

        $raw = $resp.Content
        $parsed = Try-ParseJson -Raw $raw

        return [pscustomobject]@{
            Ok = $true
            Status = [int]$resp.StatusCode
            Body = $parsed
            Raw = $raw
            Error = ""
        }
    } catch {
        $status = 0
        $raw = ""

        if ($_.Exception.Response) {
            try {
                $status = [int]$_.Exception.Response.StatusCode
            } catch {
                $status = 0
            }

            try {
                $stream = $_.Exception.Response.GetResponseStream()
                if ($stream) {
                    $reader = New-Object System.IO.StreamReader($stream)
                    $raw = $reader.ReadToEnd()
                    $reader.Close()
                }
            } catch {
                $raw = ""
            }
        }

        $parsed = Try-ParseJson -Raw $raw

        return [pscustomobject]@{
            Ok = $false
            Status = $status
            Body = $parsed
            Raw = $raw
            Error = $_.Exception.Message
        }
    }
}

function Ensure-AdminSession {
    param(
        [string]$Email,
        [string]$Password
    )

    $loginBody = @{ email = $Email; matKhau = $Password }
    $login = Invoke-Api -Method "POST" -Endpoint "/api/auth/login" -Body $loginBody

    if ($login.Ok -and $login.Body -and $login.Body.accessToken) {
        Add-Result -Name "Admin login ($Email)" -Passed $true
        return [pscustomobject]@{ Email = $Email; Token = $login.Body.accessToken; Role = $login.Body.role }
    }
    Write-Host "Admin login with default account failed (status=$($login.Status)). Trying fallback admin..." -ForegroundColor Yellow

    $fallbackEmail = "module2_admin_$(Get-Date -Format 'yyyyMMddHHmmssfff')@example.com"
    $registerBody = @{ ten = "Module2 Admin"; email = $fallbackEmail; matKhau = $Password }
    $register = Invoke-Api -Method "POST" -Endpoint "/api/auth/register/admin" -Body $registerBody

    if ($register.Ok -and $register.Body -and $register.Body.accessToken) {
        Add-Result -Name "Admin session via fallback register" -Passed $true -Detail $fallbackEmail
        return [pscustomobject]@{ Email = $fallbackEmail; Token = $register.Body.accessToken; Role = $register.Body.role }
    }

    Add-Result -Name "Admin auth (default + fallback)" -Passed $false -Detail "loginStatus=$($login.Status), registerStatus=$($register.Status)"
    return $null
}

# ============================================
# 1. CHECK BACKEND STATUS
# ============================================
Print-Header "1 - CHECK BACKEND STATUS"

$swagger = Invoke-Api -Method "GET" -Endpoint "/swagger-ui.html"
if ($swagger.Ok -and $swagger.Status -eq 200) {
    Add-Result -Name "Backend server running" -Passed $true
} else {
    Add-Result -Name "Backend server running" -Passed $false -Detail "Cannot open $BASE_URL/swagger-ui.html"
    Write-Host "Start backend first: cd backend; .\\mvnw.cmd spring-boot:run" -ForegroundColor Yellow
    exit 1
}

# ============================================
# 2. AUTHENTICATION
# ============================================
Print-Header "2 - AUTHENTICATION"

$adminSession = Ensure-AdminSession -Email $ADMIN_EMAIL -Password $ADMIN_PASS
if ($null -eq $adminSession) {
    Write-Host "Cannot get admin token. Stop test." -ForegroundColor Red
    exit 1
}

$TOKEN = $adminSession.Token
Write-Host "Using admin: $($adminSession.Email)" -ForegroundColor DarkGray

# ============================================
# 3. DANH MUC
# ============================================
Print-Header "3 - DANH MUC"

$runId = Get-Date -Format "yyyyMMddHHmmss"
$danhMucName = "Module2-DanhMuc-$runId"

$createDanhMuc = Invoke-Api -Method "POST" -Endpoint "/api/danhmuc" -Body @{ tenDanhMuc = $danhMucName } -Token $TOKEN
Add-Result -Name "Create DanhMuc" -Passed ($createDanhMuc.Ok -and $createDanhMuc.Status -eq 201) -Detail "status=$($createDanhMuc.Status)"
$DANHMUC_ID = if ($createDanhMuc.Body) { $createDanhMuc.Body.maDanhMuc } else { $null }

$getAllDanhMuc = Invoke-Api -Method "GET" -Endpoint "/api/danhmuc" -Token $TOKEN
Add-Result -Name "Get all DanhMuc" -Passed $getAllDanhMuc.Ok -Detail "status=$($getAllDanhMuc.Status)"

if ($DANHMUC_ID) {
    $getDanhMucById = Invoke-Api -Method "GET" -Endpoint "/api/danhmuc/$DANHMUC_ID" -Token $TOKEN
    Add-Result -Name "Get DanhMuc by ID" -Passed $getDanhMucById.Ok -Detail "status=$($getDanhMucById.Status)"

    $updatedDanhMucName = "$danhMucName-Updated"
    $updateDanhMuc = Invoke-Api -Method "PUT" -Endpoint "/api/danhmuc/$DANHMUC_ID" -Body @{ tenDanhMuc = $updatedDanhMucName } -Token $TOKEN
    Add-Result -Name "Update DanhMuc" -Passed $updateDanhMuc.Ok -Detail "status=$($updateDanhMuc.Status)"
}

# ============================================
# 4. NHA CUNG CAP
# ============================================
Print-Header "4 - NHA CUNG CAP"

$randPhone = Get-Random -Minimum 10000000 -Maximum 99999999
$nccPhone = "09$randPhone"
$nccName = "Module2-NCC-$runId"

$createNcc = Invoke-Api -Method "POST" -Endpoint "/api/nhacungcap" -Body @{ tenNCC = $nccName; dienThoai = $nccPhone; diaChi = "Ha Noi Test Address" } -Token $TOKEN
Add-Result -Name "Create NhaCungCap" -Passed ($createNcc.Ok -and $createNcc.Status -eq 201) -Detail "status=$($createNcc.Status)"
$NCC_ID = if ($createNcc.Body) { $createNcc.Body.maNCC } else { $null }

$getAllNcc = Invoke-Api -Method "GET" -Endpoint "/api/nhacungcap" -Token $TOKEN
Add-Result -Name "Get all NhaCungCap" -Passed $getAllNcc.Ok -Detail "status=$($getAllNcc.Status)"

if ($NCC_ID) {
    $getNccById = Invoke-Api -Method "GET" -Endpoint "/api/nhacungcap/$NCC_ID" -Token $TOKEN
    Add-Result -Name "Get NhaCungCap by ID" -Passed $getNccById.Ok -Detail "status=$($getNccById.Status)"

    $updateNcc = Invoke-Api -Method "PUT" -Endpoint "/api/nhacungcap/$NCC_ID" -Body @{ tenNCC = "$nccName-Updated"; dienThoai = $nccPhone; diaChi = "Da Nang Test Address" } -Token $TOKEN
    Add-Result -Name "Update NhaCungCap" -Passed $updateNcc.Ok -Detail "status=$($updateNcc.Status)"
}

# ============================================
# 5. SAN PHAM
# ============================================
Print-Header "5 - SAN PHAM"

$sanPhamName = "Module2-SanPham-$runId"
$createSanPham = Invoke-Api -Method "POST" -Endpoint "/api/sanpham" -Body @{ tenSanPham = $sanPhamName; giaBan = 15000000; maDanhMuc = $DANHMUC_ID; maKhachHang = $DEFAULT_KHACH_HANG_ID } -Token $TOKEN
Add-Result -Name "Create SanPham" -Passed ($createSanPham.Ok -and $createSanPham.Status -eq 201) -Detail "status=$($createSanPham.Status)"
$SANPHAM_ID = if ($createSanPham.Body) { $createSanPham.Body.maSanPham } else { $null }

$getAllSanPham = Invoke-Api -Method "GET" -Endpoint "/api/sanpham" -Token $TOKEN
Add-Result -Name "Get all SanPham" -Passed $getAllSanPham.Ok -Detail "status=$($getAllSanPham.Status)"

if ($SANPHAM_ID) {
    $getSanPhamById = Invoke-Api -Method "GET" -Endpoint "/api/sanpham/$SANPHAM_ID" -Token $TOKEN
    Add-Result -Name "Get SanPham by ID" -Passed $getSanPhamById.Ok -Detail "status=$($getSanPhamById.Status)"

    $getByDanhMuc = Invoke-Api -Method "GET" -Endpoint "/api/sanpham/danhmuc/$DANHMUC_ID" -Token $TOKEN
    Add-Result -Name "Get SanPham by DanhMuc" -Passed $getByDanhMuc.Ok -Detail "status=$($getByDanhMuc.Status)"

    $getByKhachHang = Invoke-Api -Method "GET" -Endpoint "/api/sanpham/khachhang/$DEFAULT_KHACH_HANG_ID" -Token $TOKEN
    Add-Result -Name "Get SanPham by KhachHang" -Passed $getByKhachHang.Ok -Detail "status=$($getByKhachHang.Status)"

    $updateSanPham = Invoke-Api -Method "PUT" -Endpoint "/api/sanpham/$SANPHAM_ID" -Body @{ tenSanPham = "$sanPhamName-Updated"; giaBan = 15500000; maDanhMuc = $DANHMUC_ID; maKhachHang = $DEFAULT_KHACH_HANG_ID } -Token $TOKEN
    Add-Result -Name "Update SanPham" -Passed $updateSanPham.Ok -Detail "status=$($updateSanPham.Status)"
}

# ============================================
# 6. PHIEU NHAP + CHI TIET PHIEU NHAP + TRIGGER TONKHO
# ============================================
Print-Header "6 - PHIEU NHAP + TRIGGER TONKHO"

$importQty = 10
$beforeTonKho = Invoke-Api -Method "GET" -Endpoint "/api/tonkho/kho/$DEFAULT_KHO_ID/sanpham/$SANPHAM_ID" -Token $TOKEN
$beforeQty = if ($beforeTonKho.Ok -and $beforeTonKho.Body) { [int]$beforeTonKho.Body.soLuong } else { 0 }

$createPhieuNhap = Invoke-Api -Method "POST" -Endpoint "/api/phieunhap" -Body @{ maKho = $DEFAULT_KHO_ID; maNCC = $NCC_ID; chiTiet = @(@{ maSanPham = $SANPHAM_ID; soLuong = $importQty; giaNhap = 12000000 }) } -Token $TOKEN
Add-Result -Name "Create PhieuNhap" -Passed ($createPhieuNhap.Ok -and $createPhieuNhap.Status -eq 201) -Detail "status=$($createPhieuNhap.Status)"
$PHIEUNHAP_ID = if ($createPhieuNhap.Body) { $createPhieuNhap.Body.maPhieuNhap } else { $null }

$getAllPhieuNhap = Invoke-Api -Method "GET" -Endpoint "/api/phieunhap" -Token $TOKEN
Add-Result -Name "Get all PhieuNhap" -Passed $getAllPhieuNhap.Ok -Detail "status=$($getAllPhieuNhap.Status)"

if ($PHIEUNHAP_ID) {
    $getPhieuNhapById = Invoke-Api -Method "GET" -Endpoint "/api/phieunhap/$PHIEUNHAP_ID" -Token $TOKEN
    Add-Result -Name "Get PhieuNhap by ID" -Passed $getPhieuNhapById.Ok -Detail "status=$($getPhieuNhapById.Status)"
}

$getPhieuNhapByKho = Invoke-Api -Method "GET" -Endpoint "/api/phieunhap/kho/$DEFAULT_KHO_ID" -Token $TOKEN
Add-Result -Name "Get PhieuNhap by Kho" -Passed $getPhieuNhapByKho.Ok -Detail "status=$($getPhieuNhapByKho.Status)"

$getPhieuNhapByNcc = Invoke-Api -Method "GET" -Endpoint "/api/phieunhap/nhacungcap/$NCC_ID" -Token $TOKEN
Add-Result -Name "Get PhieuNhap by NhaCungCap" -Passed $getPhieuNhapByNcc.Ok -Detail "status=$($getPhieuNhapByNcc.Status)"

$afterTonKho = Invoke-Api -Method "GET" -Endpoint "/api/tonkho/kho/$DEFAULT_KHO_ID/sanpham/$SANPHAM_ID" -Token $TOKEN
$afterQty = if ($afterTonKho.Ok -and $afterTonKho.Body) { [int]$afterTonKho.Body.soLuong } else { -1 }
$expectedAfterImport = $beforeQty + $importQty
Add-Result -Name "Trigger tang ton kho after import" -Passed ($afterTonKho.Ok -and $afterQty -eq $expectedAfterImport) -Detail "expected=$expectedAfterImport actual=$afterQty"

if ($PHIEUNHAP_ID) {
    $deletePhieuNhap = Invoke-Api -Method "DELETE" -Endpoint "/api/phieunhap/$PHIEUNHAP_ID" -Token $TOKEN
    Add-Result -Name "Delete PhieuNhap" -Passed ($deletePhieuNhap.Ok -and $deletePhieuNhap.Status -eq 204) -Detail "status=$($deletePhieuNhap.Status)"

    $afterDeleteTonKho = Invoke-Api -Method "GET" -Endpoint "/api/tonkho/kho/$DEFAULT_KHO_ID/sanpham/$SANPHAM_ID" -Token $TOKEN
    $afterDeleteQty = if ($afterDeleteTonKho.Ok -and $afterDeleteTonKho.Body) { [int]$afterDeleteTonKho.Body.soLuong } else { -1 }
    Add-Result -Name "TonKho rollback after delete phieu nhap" -Passed ($afterDeleteTonKho.Ok -and $afterDeleteQty -eq $beforeQty) -Detail "expected=$beforeQty actual=$afterDeleteQty"
}

# ============================================
# 7. TON KHO REPORT ENDPOINTS
# ============================================
Print-Header "7 - TON KHO REPORT ENDPOINTS"

$getAllTonKho = Invoke-Api -Method "GET" -Endpoint "/api/tonkho" -Token $TOKEN
Add-Result -Name "Get all TonKho" -Passed $getAllTonKho.Ok -Detail "status=$($getAllTonKho.Status)"

$getTonKhoByKho = Invoke-Api -Method "GET" -Endpoint "/api/tonkho/kho/$DEFAULT_KHO_ID" -Token $TOKEN
Add-Result -Name "Get TonKho by Kho" -Passed $getTonKhoByKho.Ok -Detail "status=$($getTonKhoByKho.Status)"

$getTonKhoBySanPham = Invoke-Api -Method "GET" -Endpoint "/api/tonkho/sanpham/$SANPHAM_ID" -Token $TOKEN
Add-Result -Name "Get TonKho by SanPham" -Passed $getTonKhoBySanPham.Ok -Detail "status=$($getTonKhoBySanPham.Status)"

$getCoTon = Invoke-Api -Method "GET" -Endpoint "/api/tonkho/kho/$DEFAULT_KHO_ID/coton" -Token $TOKEN
Add-Result -Name "Get TonKho co ton" -Passed $getCoTon.Ok -Detail "status=$($getCoTon.Status)"

$getSapHetTon = Invoke-Api -Method "GET" -Endpoint "/api/tonkho/kho/$DEFAULT_KHO_ID/saphetton" -Token $TOKEN
Add-Result -Name "Get TonKho sap het ton" -Passed $getSapHetTon.Ok -Detail "status=$($getSapHetTon.Status)"

$getTongTon = Invoke-Api -Method "GET" -Endpoint "/api/tonkho/kho/$DEFAULT_KHO_ID/tong" -Token $TOKEN
Add-Result -Name "Get Tong Ton by Kho" -Passed $getTongTon.Ok -Detail "status=$($getTongTon.Status)"

# ============================================
# 8. ERROR HANDLING
# ============================================
Print-Header "8 - ERROR HANDLING"

$invalidDanhMuc = Invoke-Api -Method "GET" -Endpoint "/api/danhmuc/999999" -Token $TOKEN
Add-Result -Name "Invalid DanhMuc ID returns 400" -Passed ((-not $invalidDanhMuc.Ok) -and $invalidDanhMuc.Status -eq 400) -Detail "status=$($invalidDanhMuc.Status)"

$noTokenDanhMuc = Invoke-Api -Method "GET" -Endpoint "/api/danhmuc"
$noTokenPassed = ((-not $noTokenDanhMuc.Ok) -and ($noTokenDanhMuc.Status -eq 401 -or $noTokenDanhMuc.Status -eq 403))
Add-Result -Name "Missing token is blocked" -Passed $noTokenPassed -Detail "status=$($noTokenDanhMuc.Status)"

# ============================================
# SUMMARY
# ============================================
Print-Header "TEST SUMMARY"

Write-Host "Total Tests : $totalTests"
Write-Host "Passed      : $passedTests" -ForegroundColor Green
Write-Host "Failed      : $failedTests" -ForegroundColor Red

if ($totalTests -gt 0) {
    $successRate = [math]::Round(($passedTests / $totalTests) * 100, 2)
    Write-Host "Success Rate: $successRate%"
}

Write-Host ""
Write-Host "Swagger UI : $BASE_URL/swagger-ui.html"
Write-Host "Database   : localhost:3306/warehouserentaldb"

if ($failedTests -eq 0) {
    Write-Host "MODULE 2 CHECK: ALL TESTS PASSED" -ForegroundColor Green
    exit 0
}

Write-Host "MODULE 2 CHECK: SOME TESTS FAILED" -ForegroundColor Red
exit 1
