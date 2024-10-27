;----------------------------------------------------------------------------------------------;
;	 SCP - DANGER BREACH /-/-/ v2.3.3                                                          ;
;                                                                                              ;
;	 This game is based on SCP Containment Breach which itself is based on the works of:       ;
;	 The SCP Foundation community (http://www.scp-wiki.net/).                                  ;
;                                                                                              ;
;    The source code is licensed under Creative Commons Attribution-ShareAlike 3.0 License.    ;
;    http://creativecommons.org/licenses/by-sa/3.0/                                            ;
;                                                                                              ;
;    See Credits.txt for a list of contributors                                                ;
;                                                                                              ;
;	 Compile with debug disabled For a smaller, Faster, And more efficent game executable.     ;
;----------------------------------------------------------------------------------------------;

Const ScpIdent$ = "SCP"
Const GameIdentShort$ = "Danger Breach"
Const GameIdent$ = ScpIdent+" - "+GameIdentShort
Const GameIdentShortAllCaps$ = "DANGER BREACH"
Const GameIdentAllCaps$ = ScpIdent+" - "+GameIdentShortAllCaps
Const GameIdentStrSeperator$ = " /-/-/ "

Const VersionNumber$ = "2.3.3"
Const SavFormatVersionNumber# = 2.0 ;only update when save data format is updated

Global EngineIdent$ = BlitzIdent()
Global EngineIdentShort$ = BlitzShortIdent()
Global EngineIdentShortest$ = BlitzShortestIdent()
Global EngineVersionNumber$ = BlitzVersion() //can't be constant because it's returned at runtime, Not compiled directly in

Const BaseGameIdent$ = "SCP - Containment Breach"
Const BaseGameVersion$ = "1.3.11"

Include "SourceCode\Main.bb"
;~IDEal Editor Parameters:
;~C#Blitz3D