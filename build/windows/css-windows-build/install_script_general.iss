;InnoSetupVersion=5.5.0
#define CSSVersion "3.2.12"

[Setup]
AppName=cs-studio
AppVersion={#CSSVersion}
DefaultDirName={pf}\cs-studio
DefaultGroupName=cs-studio
UninstallDisplayIcon={app}\cs-studio.exe
OutputBaseFilename=cs-studio-{#CSSVersion}_setup_64
Compression=lzma2
ArchitecturesInstallIn64BitMode=x64 

[Files]
Source: "..\cs-studio\*"; DestDir: "{app}"; Check: "Is64BitInstallMode"; MinVersion: 0.0,5.0; Flags: recursesubdirs
 

[Registry]
Root: HKCR; Subkey: ".opi"; ValueType: String; ValueData: "BOY"; Tasks: cssAssociation; MinVersion: 0.0,5.0; Flags: uninsdeletevalue 
Root: HKCR; Subkey: "BOY"; ValueType: String; ValueData: "BOY"; Tasks: cssAssociation; MinVersion: 0.0,5.0; Flags: uninsdeletekey 
Root: HKCR; Subkey: "BOY\DefaultIcon"; ValueType: String; ValueData: "{app}\applicationIcons\OPIRunner.ico"; Tasks: cssAssociation; MinVersion: 0.0,5.0; 
Root: HKCR; Subkey: "BOY\shell\open\command"; ValueType: String; ValueData: """{app}\cs-studio.exe"" --launcher.openFile ""%1 Position=Detached"""; Tasks: cssAssociation; MinVersion: 0.0,5.0; 
Root: HKCR; Subkey: ".css-pvtable"; ValueType: String; ValueData: "PVTable"; Tasks: cssAssociation; MinVersion: 0.0,5.0; Flags: uninsdeletevalue 
Root: HKCR; Subkey: ".pvs"; ValueType: String; ValueData: "PVTable"; Tasks: cssAssociation; MinVersion: 0.0,5.0; Flags: uninsdeletevalue 
Root: HKCR; Subkey: "PVTable"; ValueType: String; ValueData: "PVTable"; Tasks: cssAssociation; MinVersion: 0.0,5.0; Flags: uninsdeletekey 
Root: HKCR; Subkey: "PVTable\DefaultIcon"; ValueType: String; ValueData: "{app}\applicationIcons\pvtable.ico"; Tasks: cssAssociation; MinVersion: 0.0,5.0; 
Root: HKCR; Subkey: "PVTable\shell\open\command"; ValueType: String; ValueData: """{app}\cs-studio.exe"" --launcher.openFile ""%1"""; Tasks: cssAssociation; MinVersion: 0.0,5.0; 
Root: HKCR; Subkey: ".plt"; ValueType: String; ValueData: "DataBrowser"; Tasks: cssAssociation; MinVersion: 0.0,5.0; Flags: uninsdeletevalue 
Root: HKCR; Subkey: "DataBrowser"; ValueType: String; ValueData: "DataBrowser"; Tasks: cssAssociation; MinVersion: 0.0,5.0; Flags: uninsdeletekey 
Root: HKCR; Subkey: "DataBrowser\DefaultIcon"; ValueType: String; ValueData: "{app}\applicationIcons\databrowser.ico"; Tasks: cssAssociation; MinVersion: 0.0,5.0; 
Root: HKCR; Subkey: "DataBrowser\shell\open\command"; ValueType: String; ValueData: """{app}\cs-studio.exe"" --launcher.openFile ""%1"""; Tasks: cssAssociation; MinVersion: 0.0,5.0; 

[Icons]
Name: "{group}\cs-studio"; Filename: "{app}\cs-studio.exe"; MinVersion: 0.0,5.0; 

[Tasks]
Name: "cssassociation"; Description: "Associate ""css"" extensions"; GroupDescription: "File extensions:"; MinVersion: 0.0,5.0; 

[InstallDelete]
Type: filesandordirs; Name: "{app}/configuration"; 
Type: filesandordirs; Name: "{app}/p2"; 

[UninstallDelete]
Type: filesandordirs; Name: "{app}/configuration"; 
Type: filesandordirs; Name: "{app}/p2"; 

[CustomMessages]
NameAndVersion=%1 version %2
AdditionalIcons=Additional icons:
CreateDesktopIcon=Create a &desktop icon
CreateQuickLaunchIcon=Create a &Quick Launch icon
ProgramOnTheWeb=%1 on the Web
UninstallProgram=Uninstall %1
LaunchProgram=Launch %1
AssocFileExtension=&Associate %1 with the %2 file extension
AssocingFileExtension=Associating %1 with the %2 file extension...
AutoStartProgramGroupDescription=Startup:
AutoStartProgram=Automatically start %1
AddonHostProgramNotFound=%1 could not be located in the folder you selected.%n%nDo you want to continue anyway?
