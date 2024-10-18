;----------------------------------------------;
;      FUNNIMAN'S DYNAMIC LANGUAGE ENGINE	   ;
;  "this is really shit" - FUNNIMAN.EXE, 2024  ;
;											   ;
;            (c) 2024 funniman.exe			   ;
;           (c) 2024 Action Software		   ;
;            (c) 2024 Action Games			   ;
;----------------------------------------------;

Global SubTimer#, SubText$, SubText2$, SubText3$, SubText4$, SubText5$, SubY#, SubLine#, SubDelay#;, NumActiveSubtitles% ; for when you decide to allow multiple subtitles at once oscar

Global SelectedLanguage$ = GetINIString(OptionFile, "options", "language")

If SelectedLanguage = "" Then SelectedLanguage = "English"

;Function SetSubMSG(txt$="Placeholder Text", Sec# = 8.0, txt2$="", txt3$="", txt4$="", txt5$="", sLine#=1, sDelay#=0)
Function SetSubMSG(txt$="Placeholder Text", Sec# = 8.0, txt2$="", txt3$="", txt4$="", txt5$="", sDelay#=0);set the subtitle paramaters
	
	SubText = txt
	SubText2 = txt2
	SubText3 = txt3
	SubText4 = txt4
	SubText5 = txt5
	SubTimer = 70.0 * Sec
	SubY = 0.0
	SubLine = 1.0
	SubDelay = 70.0 * sDelay
	
End Function

Function UpdateSubMSG();update subtitles
	Local scale# = GraphicHeight/768.0
	;Local width% = 200*scale
	;Local width = AAStringWidth(SubText)+(20*scale)
	Local height% = 30*scale
	Local oldHeight = height
	
	If SubText <> ""
		;Replace:SubText(subString:"\n",withString:Chr(13))
		If SubDelay < 0.0
			If SubText2 <> "" Then height = height + (oldHeight/2)
			If SubText3 <> "" Then height = height + (oldHeight/2)
			If SubText4 <> "" Then height = height + (oldHeight/2)
			If SubText5 <> "" Then height = height + (oldHeight/2)
			If SubTimer > 0.0
				If SubY < oldHeight
					SubY = Min(SubY+(2*FPSfactor2),oldHeight)
				Else
					SubY = oldHeight
				EndIf
				DebugLog "SubY is now "+SubY
				SubTimer = SubTimer - FPSfactor2
				DebugLog "SubTimer is now "+SubTimer
			Else 
				If SubY > 0.0
					SubY = Max(SubY-(2*FPSfactor2),0.0)
					DebugLog "SubY is now "+SubY
				Else
					SubText = ""
					SubText2 = ""
					SubText3 = ""
					SubText4 = ""
					SubText5 = ""
					SubTimer = 0.0
					SubY = 0.0
					SubLine = 0.0
					SubDelay = 0.0
					
					;NumActiveSubtitles = NumActiveSubtitles -1
					
					DebugLog "Sub(s) reset"
				EndIf
			EndIf
		Else
			SubDelay = SubDelay - FPSfactor2
		EndIf
	EndIf
	
End Function

Function DrawSubMSG();draw subtitles to screen
	Local scale# = GraphicHeight/768.0
	;Local width% = 200*scale
	Local width = AAStringWidth(SubText)+(20*scale)
	Local height% = 30*scale
	
	If (Len(SubText2) > Len(SubText) Or Len(SubText2) = Len(SubText)) And (Len(SubText2) > Len(SubText3) Or Len(SubText2) = Len(SubText3)) And (Len(SubText2) > Len(SubText4) Or Len(SubText2) = Len(SubText4)) And (Len(SubText2) > Len(SubText5) Or Len(SubText2) = Len(SubText5))
		width = AAStringWidth(SubText2)+(20*scale)
	ElseIf (Len(SubText3) > Len(SubText) Or Len(SubText3) = Len(SubText)) And (Len(SubText3) > Len(SubText2) Or Len(SubText3) = Len(SubText2)) And (Len(SubText3) > Len(SubText4) Or Len(SubText3) = Len(SubText4)) And (Len(SubText3) > Len(SubText5) Or Len(SubText3) = Len(SubText5))
		width = AAStringWidth(SubText3)+(20*scale)
	ElseIf (Len(SubText4) > Len(SubText) Or Len(SubText4) = Len(SubText)) And (Len(SubText4) > Len(SubText2) Or Len(SubText4) = Len(SubText2)) And (Len(SubText4) > Len(SubText3) Or Len(SubText4) = Len(SubText3)) And (Len(SubText4) > Len(SubText5) Or Len(SubText4) = Len(SubText5))
		width = AAStringWidth(SubText4)+(20*scale)
	ElseIf (Len(SubText5) > Len(SubText) Or Len(SubText5) = Len(SubText)) And (Len(SubText5) > Len(SubText2) Or Len(SubText5) = Len(SubText2)) And (Len(SubText5) > Len(SubText3) Or Len(SubText5) = Len(SubText3)) And (Len(SubText5) > Len(SubText4) Or Len(SubText5) = Len(SubText4))
		width = AAStringWidth(SubText5)+(20*scale)
	EndIf
	
	Local x% = (GraphicWidth/2)-(width/2)
	Local y% = (-height)+SubY
	
	If SubText <> ""
		If SubDelay < 0.0
			Local oldHeight = height
			AASetFont Font1
			Color 255,255,255
			Local i% = 0
			If SubText2 <> "" Then
				height = height + (oldHeight/2)
				If SubText3 <> "" Then 
					height = height + (oldHeight/2) 
					If SubText4 <> "" Then 
						height = height + (oldHeight/2)
						If SubText5 <> "" Then 
							height = height + (oldHeight/2)
						EndIf
					EndIf
				EndIf
			EndIf
			DrawFrame(x,y,width,height)
			AAText(GraphicWidth/2,y+(oldHeight/2),SubText,True,True)
			AAText(GraphicWidth/2,y+oldHeight,SubText2,True,True)
			AAText(GraphicWidth/2,y+(oldHeight/2)+oldHeight,SubText3,True,True)
			AAText(GraphicWidth/2,y+(oldHeight/2)+(oldHeight*1.5),SubText4,True,True)
			AAText(GraphicWidth/2,y+(oldHeight/2)+(oldHeight*2),SubText5,True,True)
			DebugLog "Drawn Text and Frame"+SubY
		EndIf
	EndIf
	
End Function

Function LoadLanguageString$(file$,name$,param$="text")
	Local searchPath$ = ValidateLanguagePath("Data\lang\"+SelectedLanguage,file)
	;Local tmp$
	
	;If FileType(searchPath)=0 Then
	;	PutINIValue(OptionFile,"options","language","English")
	;	RuntimeError("INVALID LANGUAGE PATH: '"+searchPath+"'. ENTRY WILL BE RESET UPON NEXT STARTUP.")
	;EndIf
	
	;If (MemeMode)
	;	searchPath = searchPath + "\meme"
	;	If FileType(searchPath)=0 Then
	;		PutINIValue(OptionFile,"options","language","English")
	;		RuntimeError("INVALID LANGUAGE PATH: '"+searchPath+"'. ENTRY WILL BE RESET UPON NEXT STARTUP.")
	;	EndIf
	;EndIf
	
	;searchPath=searchPath+"\"+file
	
	;If FileType(searchPath)=0 Then RuntimeError("ILLEGAL LANGUAGE: '"+searchPath+"'. LANGUAGE MUST INCLUDE '"+file+"'.")
	
	;DebugLog name

	Local tmp$ = GetINIString(searchPath, name, param)
	If tmp$ = "" Then tmp = name
	
	Return tmp
End Function

Function ValidateLanguagePath$(path$,file$)
	If FileType(path)=0 Then
		PutINIValue(OptionFile,"options","language","English")
		RuntimeError("INVALID LANGUAGE PATH: '"+path+"'. ENTRY WILL BE RESET UPON NEXT STARTUP.")
	EndIf
	
	Local tmp$ = path
	
	If (MemeMode)
		tmp = tmp + "\meme"
		If FileType(tmp)=0 Then
			PutINIValue(OptionFile,"options","language","English")
			RuntimeError("INVALID LANGUAGE PATH: '"+tmp+"'. ENTRY WILL BE RESET UPON NEXT STARTUP.")
		EndIf
	EndIf
	
	Local tmpF$=tmp+"\"+file
	
	If FileType(path)=0 Then RuntimeError("ILLEGAL LANGUAGE: '"+tmp+"'. LANGUAGE MUST INCLUDE '"+file+"'.")
	
	Return tmpF
End Function

Function LoadSubtitles(name$);aquire subtitles
	
	Local TempFloat# = 0
	Local TempFloat2# = 0
	Local TempFloat3# = 0
	Local TempStr3$ = ""
	Local TempStr4$ = ""
	Local TempStr5$ = ""
	Local TempStr6$ = ""
	Local TempStr7$ = ""
	
	ValidateLanguagePath("Data\lang\"+SelectedLanguage,langSubtitlesF)
	
	;Local subtitlesSearchPath$ = "Data\lang\"+SelectedLanguage
	
	;If FileType(subtitlesSearchPath)=0 Then
	;	PutINIValue(OptionFile,"options","language","English")
	;	RuntimeError("INVALID LANGUAGE PATH: '"+subtitlesSearchPath+"'. ENTRY WILL BE RESET UPON NEXT STARTUP.")
	;EndIf
	
	;If (MemeMode) 
	;	subtitlesSearchPath = subtitlesSearchPath + "\meme"
	;	If FileType(subtitlesSearchPath)=0 Then
	;		PutINIValue(OptionFile,"options","language","English")
	;		RuntimeError("INVALID LANGUAGE PATH: '"+subtitlesSearchPath+"'. ENTRY WILL BE RESET UPON NEXT STARTUP.")
	;	EndIf
	;EndIf
	
	;subtitlesSearchPath=subtitlesSearchPath+"\"+langSubtitlesF
	
	;If FileType(subtitlesSearchPath)=0 Then RuntimeError("ILLEGAL LANGUAGE: '"+subtitlesSearchPath+"'. LANGUAGE MUST INCLUDE '"+langSubtitlesF+"'.")
	
	DebugLog name
	
	;This mental insanity is here to prevent b3d from shitting itself by limiting what sounds it is able to display subtitles for
	;also because im shit at optimizing programming
	;...
	;I am not sorry
	
	If SubtitlesEnabled Then
		DebugLog "Subtitles Passed Check 1-1"
		If (Instr(name,"SFX\Step") < 1) And (Instr(name,"SFX\Music") < 1) And (Instr(name,"SFX\Interact") < 1) And (Instr(name,"SFX\Horror") < 1) Then
			DebugLog "Subtitles Passed Check 1-2"
			If (Instr(name,"SFX\General") < 1) And (Instr(name,"SFX\Door") < 1) And (Instr(name,"SFX\Ambient") < 1) And (Instr(name,"SFX\Alarm") < 1 Or name = "SFX\Alarm\Alarm2_1.ogg") Then
				DebugLog "Subtitles Passed Check 1-3"
				If (Instr(name,"SFX\Room\Intro\Guard\Music") < 1) And (Instr(name,"SFX\Character\MTF\Step") < 1) And (Not name = "SFX\Room\Intro\PA\on.ogg") Then
					DebugLog "Subtitles Passed Check 1-4"
					If (Not name = "SFX\Room\Intro\PA\off.ogg") And (Not name = "SFX\Room\Intro\173Chamber.ogg") And (Not name = "SFX\Room\Intro\See173.ogg") And (Not name = "SFX\Room\Intro\173Vent.ogg") Then
						DebugLog "Subtitles Passed Check 1-5"
						If (Instr(name,"SFX\Room\Intro\Light") < 1) And (Instr(name,"SFX\Room\Intro\Bang") < 1) And (Not name = "SFX\Room\Intro\ClassD\DontLikeThis.ogg") And (Not name = "SFX\Room\Intro\ClassD\Gasp.ogg") Then
							DebugLog "Subtitles Passed Check 1-7"
							If (Not name = "SFX\Room\Intro\Horror.ogg") And (Instr(name,"SFX\Room\Intro\Commotion") < 1) And (Instr(name,"SFX\Character\D9341") < 1) And (Instr(name,"SFX\Character\MTF\Breath") < 1) Then
								DebugLog "Subtitles Passed Check 1-8"
								If (Instr(name,"SFX\SCP\066\Notes") < 1) And (Instr(name,"SFX\SCP\096") < 1) And (Instr(name,"SFX\SCP\106") < 1) And (Instr(name,"SFX\SCP\173") < 1) And (Instr(name,"SFX\SCP\205") < 1) Then
									DebugLog "Subtitles Passed Check 1-9"
									If (Instr(name,"SFX\SCP\294") < 1) And (Instr(name,"SFX\SCP\372") < 1) And (Instr(name,"SFX\SCP\427") < 1) And (Instr(name,"SFX\SCP\513") < 1) And (Instr(name,"SFX\SCP\682") < 1) Then
										DebugLog "Subtitles Passed Check 1-10"
										If (Instr(name,"SFX\SCP\860") < 1) And (Instr(name,"SFX\SCP\914") < 1) And (Instr(name,"SFX\SCP\966") < 1) And (Instr(name,"SFX\SCP\966") < 1) And (Instr(name,"SFX\SCP\1048A") < 1) And (Instr(name,"SFX\SCP\1162") < 1) Then
											DebugLog "Subtitles Passed Check 1-11"
											If (Instr(name,"SFX\SCP\1499") < 1) And (Instr(name,"SFX\SCP\Joke\Saxophone") < 1) And (Instr(name,"SFX\Room\Tesla") < 1) And (Instr(name,"SFX\SCP\066\Rolling") < 1) And (Instr(name,"SFX\SCP\066\Beethoven") < 1) Then
												DebugLog "Subtitles Passed Check 1-12"
												If (Instr(name,"SFX\Radio\scpradio") < 1) And (Instr(name,"SFX\Radio\static") < 1) And (Instr(name,"SFX\Radio\RadioAlarm") < 1) And (Instr(name,"SFX\Radio\squelch") < 1) And (Instr(name,"SFX\Radio\Buzz") < 1) And (Instr(name,"SFX\Radio\UserTracks\") < 1) Then
													DebugLog "Subtitles Passed Check 1-13"
													;TempStr3 = GetINIString(subtitlesSearchPath, name, "text")
													;If TempStr3 <> "" Then;specified subtitles are valid
													;	DebugLog "Subtitles Passed Check 2"
													;	TempStr4 = GetINIString(subtitlesSearchPath, name, "text2")
													;	If TempStr4 <> "" Then;multiple lines of text
													;		DebugLog "Subtitles Passed Optional Check 1"
													;		;TempFloat = GetINIFloat(subtitlesSearchPath, name, "line");get specified longest line
													;		TempStr5 = GetINIString(subtitlesSearchPath, name, "text3")
													;		If TempStr5 <> "" Then
													;			TempStr6 = GetINIString(subtitlesSearchPath, name, "text4")
													;			If TempStr6 <> "" Then TempStr7 = GetINIString(subtitlesSearchPath, name, "text5")
													;		EndIf
													;	EndIf
													
													TempStr3 = LoadLanguageString(langSubtitlesF,name,"text")
													TempStr4 = LoadLanguageString(langSubtitlesF,name,"text2")
													TempStr5 = LoadLanguageString(langSubtitlesF,name,"text3")
													TempStr6 = LoadLanguageString(langSubtitlesF,name,"text4")
													TempStr7 = LoadLanguageString(langSubtitlesF,name,"text5")
													
													If (TempStr3 = name) Then TempStr3 = ""
													If (TempStr4 = name) Then TempStr4 = ""
													If (TempStr5 = name) Then TempStr5 = ""
													If (TempStr6 = name) Then TempStr6 = ""
													If (TempStr7 = name) Then TempStr7 = ""
													
													;TempFloat2 = GetINIFloat(subtitlesSearchPath, name, "time")
													TempFloat2 = Float(LoadLanguageString(langSubtitlesF,name,"time"))
													
													;TempFloat3 = GetINIFloat(subtitlesSearchPath, name, "delay")
													TempFloat3 = Float(LoadLanguageString(langSubtitlesF,name,"delay"))
													;If TempFloat3 = 0 Then TempFloat3 = 0.0; what
													
													;If TempFloat = 0 Then TempFloat = 1.0;no specified longest line
													
													DebugLog TempStr3
													
													;SetSubMSG(TempStr3,TempFloat2,TempStr4,TempStr5,TempStr6,TempStr7,TempFloat,TempFloat3)
													SetSubMSG(TempStr3,TempFloat2,TempStr4,TempStr5,TempStr6,TempStr7,TempFloat3);set subtitle paramaters
													
													;NumActiveSubtitles = NumActiveSubtitles + 1
													CreateConsoleMsg("Displayed Subtitles: "+TempStr3)
													
													DebugLog "Displayed Subtitles: "+TempStr3
													;free memory
													;[Block]
													TempFloat = 0.0
													TempFloat2 = 0.0
													TempFloat3 = 0.0
													TempStr3 = ""
													TempStr4 = ""
													TempStr5 = ""
													TempStr6 = ""
													TempStr7 = ""
													;[End Block]
													;Else ;specified subtitles are not valid
													;	CreateConsoleMsg("Sound " + Chr(34) + name + Chr(34) + " does not contain a valid subtitles entry.")
													;	DebugLog name + " aint got subtitles"
													;EndIf
												EndIf
											EndIf
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D