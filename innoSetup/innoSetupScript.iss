﻿; Script generated by the Inno Script Studio Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "SubtitlesDownloader"
; should be in sync with maven pom file!
#define MyAppVersion "1.5.0"
#define MyAppPublisher "Rafal Magda"
#define MyAppURL "https://bitbucket.org/rafalmag/subtitlesdownloader/"
#define MyAppExeName "SubtitlesDownloader.exe"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{0264BFDA-A46D-448E-9CAA-AAFAACC54526}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\{#MyAppName}
DefaultGroupName={#MyAppName}
AllowNoIcons=yes
LicenseFile=..\LICENSE.txt
InfoBeforeFile=..\about.txt
OutputDir=..\target
OutputBaseFilename=SubtitlesDownloaderSetup
Compression=lzma
SolidCompression=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "polish"; MessagesFile: "compiler:Languages\Polish.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "..\target\SubtitlesDownloader.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\target\subtitlesdownloader-{#MyAppVersion}-SNAPSHOT.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\src\main\resources\icon16.png"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\src\main\resources\icon2.ico"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\src\main\resources\icon24.png"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\src\main\resources\icon32.png"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\src\main\resources\icon64.png"; DestDir: "{app}"; Flags: ignoreversion
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; WorkingDir: "{app}"; IconFilename: "{app}\icon2.ico"; IconIndex: 0
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\icon2.ico"; IconIndex: 0; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: shellexec postinstall skipifsilent

[CustomMessages]
english.JreMissing=Oracle Java v1.8 or newer not found in the system. Java 1.8 or later is required to run this application (can be installed after this installation too). Do you want to continue?
polish.JreMissing=Oracle Java v1.8 lub nowsza nie została znaleziona. Java 1.8 lub nowsza jest wymagana przez tą aplikację (może być zainstalowana zaraz po). Czy chcesz kontynować?
english.shellMenu=Find subtitles with SubtitlesDownloader
polish.shellMenu=Znajdź napisy z SubtitlesDownloader

[Registry]
Root: "HKCR"; Subkey: "SystemFileAssociations\.asf\shell\subtitlesdownloader"; ValueType: string; ValueData: "{cm:shellMenu}"; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.asf\shell\subtitlesdownloader\command"; ValueType: string; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.avi\shell\subtitlesdownloader"; ValueType: string; ValueData: "{cm:shellMenu}"; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.avi\shell\subtitlesdownloader\command"; ValueType: string; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.divx\shell\subtitlesdownloader"; ValueType: string; ValueData: "{cm:shellMenu}"; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.divx\shell\subtitlesdownloader\command"; ValueType: string; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.mkv\shell\subtitlesdownloader"; ValueType: string; ValueData: "{cm:shellMenu}"; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.mkv\shell\subtitlesdownloader\command"; ValueType: string; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.mov\shell\subtitlesdownloader"; ValueType: string; ValueData: "{cm:shellMenu}"; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.mov\shell\subtitlesdownloader\command"; ValueType: string; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.mp4\shell\subtitlesdownloader"; ValueType: string; ValueData: "{cm:shellMenu}"; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.mp4\shell\subtitlesdownloader\command"; ValueType: string; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.mpeg\shell\subtitlesdownloader"; ValueType: string; ValueData: "{cm:shellMenu}"; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.mpeg\shell\subtitlesdownloader\command"; ValueType: string; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.mpg\shell\subtitlesdownloader"; ValueType: string; ValueData: "{cm:shellMenu}"; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.mpg\shell\subtitlesdownloader\command"; ValueType: string; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.ogg\shell\subtitlesdownloader"; ValueType: string; ValueData: "{cm:shellMenu}"; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.ogg\shell\subtitlesdownloader\command"; ValueType: string; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.ogm\shell\subtitlesdownloader"; ValueType: string; ValueData: "{cm:shellMenu}"; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.ogm\shell\subtitlesdownloader\command"; ValueType: string; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.ogv\shell\subtitlesdownloader"; ValueType: string; ValueData: "{cm:shellMenu}"; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.ogv\shell\subtitlesdownloader\command"; ValueType: string; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.rm\shell\subtitlesdownloader"; ValueType: string; ValueData: "{cm:shellMenu}"; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.rm\shell\subtitlesdownloader\command"; ValueType: string; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.rmvb\shell\subtitlesdownloader"; ValueType: string; ValueData: "{cm:shellMenu}"; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.rmvb\shell\subtitlesdownloader\command"; ValueType: string; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.wmv\shell\subtitlesdownloader"; ValueType: string; ValueData: "{cm:shellMenu}"; Flags: createvalueifdoesntexist deletekey deletevalue
Root: "HKCR"; Subkey: "SystemFileAssociations\.wmv\shell\subtitlesdownloader\command"; ValueType: string; ValueData: """{app}\{#MyAppExeName}"" ""%1"""; Flags: createvalueifdoesntexist deletekey deletevalue

[Code]
function InitializeSetup(): Boolean;
var
 ErrorCode: Integer;
 JavaInstalled : Boolean;
 ResultMsg : Boolean;
 Versions: TArrayOfString;
 I: Integer;
 regRoot: Integer;
begin
 // Check which view of registry should be taken:
 regRoot := HKLM
 begin
  if IsWin64 then
  begin
   regRoot := HKLM64
  end;
 end;
 
 if (RegGetSubkeyNames(regRoot, 'SOFTWARE\JavaSoft\Java Runtime Environment', Versions)) or (RegGetSubkeyNames(regRoot, 'SOFTWARE\JavaSoft\Java Development Kit', Versions)) then
 begin
  for I := 0 to GetArrayLength(Versions)-1 do
  begin
   Log('Versions[' + IntToStr(I) + ']: ' + Versions[I]);
   if JavaInstalled = true then
   begin
    //do nothing
   end else
   begin
    if ( Versions[I][2]='.' ) and ( ( StrToInt(Versions[I][1]) > 1 ) or ( ( StrToInt(Versions[I][1]) = 1 ) and ( StrToInt(Versions[I][3]) >= 8 ) ) ) then
    begin
     JavaInstalled := true;
    end else
    begin
     JavaInstalled := false;
    end;
   end;
  end;
 end else
 begin
  JavaInstalled := false;
 end;

 if JavaInstalled then
 begin
  Result := true;
 end else
 begin
  ResultMsg := MsgBox('{cm:JreMissing}',
   mbConfirmation, MB_YESNO) = idYes;
  if ResultMsg = false then
  begin
   Result := false;
  end else
  begin
   Result := true;
   ShellExec('open',
    'http://www.java.com/getjava/',
    '','',SW_SHOWNORMAL,ewNoWait,ErrorCode);
  end;
 end;
end;

end.
