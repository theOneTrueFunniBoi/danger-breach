;achievement menu & messages by InnocentSam


DebugLog("Indexing Achievements...")

Const MAXACHIEVEMENTS=48
Dim Achievements%(MAXACHIEVEMENTS)

Const AchvIntro%=0, AchvHeavy%=1, AchvEntrance%=2, Achv005%=3, Achv008%=4, Achv009%=5, Achv012%=6, Achv018%=7, Achv035%=8, Achv049%=9, Achv055=10, Achv066=11
Const Achv079%=12, Achv096%=13, Achv106%=14, Achv148%=15, Achv178=16, Achv205=17, Achv268=18, Achv294%=19, Achv372%=20, Achv420%=21, Achv427=22, Achv500%=23
Const Achv513%=24, Achv714%=25, Achv789%=26, Achv860%=27, Achv895%=28, Achv914%=29, Achv939%=30, Achv966%=31, Achv970=32, Achv1025%=33, Achv1048=34, Achv1123=35

Const Achv1981=36, AchvFurry%=37, AchvMaynard%=38, AchvHarp%=39, AchvSNAV%=40, AchvOmni%=41, AchvTesla%=42, AchvPD%=43

Const Achv1162% = 44, Achv1499% = 45, AchvScrabble% = 46

Const AchvKeter% = 47, AchvConsole%=48

Global UsedConsole

DebugLog("All Achievements indexed.")

Local iCopy$

Global AchievementsMenu%
Global AchvMSGenabled% = GetINIInt(OptionFile, "options", "achievement popup enabled")
Dim AchievementStrings$(MAXACHIEVEMENTS)
Dim AchievementDescs$(MAXACHIEVEMENTS)
Dim AchvIMG%(MAXACHIEVEMENTS)
For i = 0 To MAXACHIEVEMENTS-1
	Local loc2% = GetINISectionLocation("Data\achievementstrings.ini", "s"+Str(i))
	AchievementStrings(i) = GetINIString2("Data\achievementstrings.ini", loc2, "string1")
	AchievementDescs(i) = GetINIString2("Data\achievementstrings.ini", loc2, "AchvDesc")
	
	Local image$ = GetINIString2("Data\achievementstrings.ini", loc2, "image") 
	
	If FileSize("GFX\menu\achievements\"+image+".jpg") = 0 Then
		AchvIMG(i) = LoadImage_Strict("GFX\menu\achievements\Achv055.jpg")
		DebugLog("Achv Image for AchvID: "+i+" was not found. Using 055's achv image.")
		iCopy=iCopy+", "+image
	Else
		AchvIMG(i) = LoadImage_Strict("GFX\menu\achievements\"+image+".jpg")
		DebugLog("Achv Image: '"+image+".jpg' precached.")
	EndIf
	
	AchvIMG(i) = ResizeImage2(AchvIMG(i),ImageWidth(AchvIMG(i))*GraphicHeight/768.0,ImageHeight(AchvIMG(i))*GraphicHeight/768.0)
Next

Global AchvLocked = LoadImage_Strict("GFX\menu\achievements\achvlocked.jpg")
AchvLocked = ResizeImage2(AchvLocked,ImageWidth(AchvLocked)*GraphicHeight/768.0,ImageHeight(AchvLocked)*GraphicHeight/768.0)

DebugLog("Locked Achv Image precached.")

Function GiveAchievement(achvname%, showMessage%=True)
	If Achievements(achvname)<>True Then
		Achievements(achvname)=True
		If AchvMSGenabled And showMessage Then
			Local loc2% = GetINISectionLocation("Data\achievementstrings.ini", "s"+achvname)
			Local AchievementName$ = GetINIString2("Data\achievementstrings.ini", loc2, "string1")
			;Msg = "Achievement Unlocked - "+AchievementName
			;MsgTimer=70*7
			CreateAchievementMsg(achvname,AchievementName)
			
			Local image$ = GetINIString2("Data\achievementstrings.ini", loc2, "image")
			
			If FileSize("GFX\menu\achievements\"+image+".jpg") = 0 Then
			
				Msg = "The Achievement icon: 'GFX\menu\achievements\"+image+".jpg' was not found. Please check your installed files are valid."
				MsgTimer = 20*70
				Color 255,0,0
				
			EndIf
		EndIf
		
		DebugLog("Achv of ID: "+achvname+" has been awarded.")
	EndIf
End Function

Function AchievementTooltip(achvno%)
    Local scale# = GraphicHeight/768.0
    
    AASetFont Font3
    Local width = AAStringWidth(AchievementStrings(achvno))
    AASetFont Font1
    If (AAStringWidth(AchievementDescs(achvno))>width) Then
        width = AAStringWidth(AchievementDescs(achvno))
    EndIf
    width = width+20*MenuScale
    
    Local height = 38*scale
    
    Color 25,25,25
    Rect(ScaledMouseX()+(20*MenuScale),ScaledMouseY()+(20*MenuScale),width,height,True)
    Color 150,150,150
    Rect(ScaledMouseX()+(20*MenuScale),ScaledMouseY()+(20*MenuScale),width,height,False)
    AASetFont Font3
    AAText(ScaledMouseX()+(20*MenuScale)+(width/2),ScaledMouseY()+(35*MenuScale), AchievementStrings(achvno), True, True)
    AASetFont Font1
    AAText(ScaledMouseX()+(20*MenuScale)+(width/2),ScaledMouseY()+(55*MenuScale), AchievementDescs(achvno), True, True)
End Function

Function DrawAchvIMG(x%, y%, achvno%)
	Local row%
	Local scale# = GraphicHeight/768.0
	Local SeparationConst2 = 76 * scale
;	If achvno >= 0 And achvno < 4 Then 
;		row = achvno
;	ElseIf achvno >= 3 And achvno <= 6 Then
;		row = achvno-3
;	ElseIf achvno >= 7 And achvno <= 10 Then
;		row = achvno-7
;	ElseIf achvno >= 11 And achvno <= 14 Then
;		row = achvno-11
;	ElseIf achvno >= 15 And achvno <= 18 Then
;		row = achvno-15
;	ElseIf achvno >= 19 And achvno <= 22 Then
;		row = achvno-19
;	ElseIf achvno >= 24 And achvno <= 26 Then
;		row = achvno-24
;	EndIf
	row = achvno Mod 4
	Color 0,0,0
	Rect((x+((row)*SeparationConst2)), y, 64*scale, 64*scale, True)
	If Achievements(achvno) = True Then
		DrawImage(AchvIMG(achvno),(x+(row*SeparationConst2)),y)
	Else
		DrawImage(AchvLocked,(x+(row*SeparationConst2)),y)
	EndIf
	Color 50,50,50
	
	Rect((x+(row*SeparationConst2)), y, 64*scale, 64*scale, False)
End Function

Global CurrAchvMSGID% = 0

Type AchievementMsg
	Field achvID%
	Field txt$
	Field msgx#
	Field msgtime#
	Field msgID%
End Type

Function CreateAchievementMsg.AchievementMsg(id%,txt$)
	Local amsg.AchievementMsg = New AchievementMsg
	
	amsg\achvID = id
	amsg\txt = txt
	amsg\msgx = 0.0
	amsg\msgtime = FPSfactor2
	amsg\msgID = CurrAchvMSGID
	CurrAchvMSGID = CurrAchvMSGID + 1
	
	Return amsg
End Function

Function UpdateAchievementMsg()
	Local amsg.AchievementMsg,amsg2.AchievementMsg
	Local scale# = GraphicHeight/768.0
	Local width% = 264*scale
	Local height% = 84*scale
	Local x%,y%
	Local achvTemp$
	
	For amsg = Each AchievementMsg
		If amsg\msgtime <> 0
			x=GraphicWidth+amsg\msgx
			y=(GraphicHeight-height)
			For amsg2 = Each AchievementMsg
				If amsg2 <> amsg
					If amsg2\msgID > amsg\msgID
						y=y-height
					EndIf
				EndIf
			Next
			DrawFrame(x,y,width,height)
			Color 0,0,0
			Rect(x+10*scale,y+10*scale,64*scale,64*scale,True)
			DrawImage(AchvIMG(amsg\achvID),x+10*scale,y+10*scale)
			Color 50,50,50
			Rect(x+10*scale,y+10*scale,64*scale,64*scale,False)
			Color 255,255,255
			AASetFont Font1
			If amsg\txt = ":3" Then
				achvTemp$ = "Achievement Unlocked - "+amsg\txt
				For i = 0 To Rand(10,15)
					achvTemp$ = Replace("Achievement Unlocked - "+amsg\txt,Mid("Achievement Unlocked - "+amsg\txt,Rand(1,Len(achvTemp)-1),1),Chr(Rand(130,250)))
				Next
				RowText(achvTemp$,x+84*scale,y+10*scale,width-94*scale,y-20*scale)
			Else
				RowText("Achievement Unlocked - "+amsg\txt,x+84*scale,y+10*scale,width-94*scale,y-20*scale)
			EndIf
			If amsg\msgtime > 0.0 And amsg\msgtime < 70*7
				amsg\msgtime = amsg\msgtime + FPSfactor2
				If amsg\msgx > -width%
					amsg\msgx = Max(amsg\msgx-4*FPSfactor2,-width%)
				EndIf
			ElseIf amsg\msgtime >= 70*7
				amsg\msgtime = -1
			ElseIf amsg\msgtime = -1
				If amsg\msgx < 0.0
					amsg\msgx = Min(amsg\msgx+4*FPSfactor2,0.0)
				Else
					amsg\msgtime = 0.0
				EndIf
			EndIf
		Else
			Delete amsg
		EndIf
	Next
	
End Function




;~IDEal Editor Parameters:
;~C#Blitz3D