; Script generated by the Inno Script Studio Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "SubtitlesDownloader"
#define MyAppVersion "1.4.0"
#define MyAppPublisher "Rafal Magda"
#define MyAppURL "https://bitbucket.org/rafalmag/subtitlesdownloader/"
#define MyAppExeName "subtitlesDownloader.bat"

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
OutputDir=.\tmp
OutputBaseFilename=SubtitlesDownloaderSetup
Compression=lzma
SolidCompression=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "polish"; MessagesFile: "compiler:Languages\Polish.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "..\subtitlesDownloader.bat"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\target\subtitlesdownloader-1.3.6-SNAPSHOT.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\src\main\resources\icon16.png"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\src\main\resources\icon2.ico"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\src\main\resources\icon24.png"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\src\main\resources\icon32.png"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\src\main\resources\icon64.png"; DestDir: "{app}"; Flags: ignoreversion
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\icon2.ico"; IconIndex: 0
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\icon2.ico"; IconIndex: 0; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: shellexec postinstall skipifsilent
