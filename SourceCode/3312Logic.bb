;Contains all logic for the SCP-3312 Infect Function
;Will also contain all logic for creating the SCP-3312 monitor image

Global Infect3312#, InfectType3312#, InfectOverride3312#

Const stringTempThingB$ = "your wone of wus now :3"
Const stringTempThingA$ = "last i checked, did you check, i can't remember anymore, it's all they let me think about, i don't think im human"
Const stringTempThing2$ = "your mental age is slipping...if you even know what mental means anymore..."

Function Update3312Infect()
	Local temp#
	Local stringTemp$	
	Local tex970reload#
	
	If (Not Wearing714) Then
	If Infect3312 > 0 Then
		If Infect3312 < 90.0 Then
			temp=Infect3312
			If SelectedDifficulty\otherFactors = EASY Then Infect3312 = Min(Infect3312+FPSfactor*0.002,100)
			If SelectedDifficulty\otherFactors = NORMAL Then Infect3312 = Min(Infect3312+FPSfactor*0.004,100)
			If SelectedDifficulty\otherFactors = HARD Then Infect3312 = Min(Infect3312+FPSfactor*0.008,100)
			If InfectType3312=0
				If Infect3312 > 1.01 And temp =< 1.01 Then
					Msg = "You cringe at the webpage open on the monitor."
					MsgTimer = 70*6
				Else If Infect3312 > 10 And temp =< 10.0
					Msg = "The artwork on the webpage is begining to interest you."
					MsgTimer = 70*6
					PlaySound_Strict(HorrorSFX(11))
				Else If Infect3312 > 20 And temp =< 20.0
					Msg = "You can't stop thinking about that artwork..."
					MsgTimer = 70*6
					PlaySound_Strict(HorrorSFX(6))
				Else If Infect3312 > 40 And temp =< 40.0
					Msg = "You are begining to experience difficulty thinking straight..."
					MsgTimer = 70*6
					PlaySound_Strict(HorrorSFX(0))
					If (Not furri) Then ;ah yes, a furry putting a furry in a non furry game, who would've expected this
						HideEntity Curr173\obj
						HideEntity Curr173\obj3
						Local temp2# = EntityScaleX(Curr173\obj)
						Curr173\obj = LoadAnimMesh_Strict("GFX\npcs\173bodyOwO.b3d")
						Curr173\obj3 = LoadAnimMesh_Strict("GFX\npcs\173headOwO.b3d")
						Animate Curr173\obj, 1, 0.05
						ScaleEntity Curr173\obj, temp2,temp2,temp2
						ScaleEntity Curr173\obj3, temp2,temp2,temp2
						ShowEntity Curr173\obj
						ShowEntity Curr173\obj3
						
						Curr173\Speed = (GetINIFloat("DATA\NPCs.ini", "SCP-173", "speed") / 100.0)
						If halloween990 Then
							If (Not HalloweenTex) Then
								tex970reload = LoadTexture_Strict("GFX\npcs\173h.pt", 1)
								EntityTexture Curr173\obj, tex970reload, 0, 0
								EntityTexture Curr173\obj3, tex970reload, 0, 0
								FreeTexture tex970reload
							EndIf
						EndIf
						furri = True
						CreateConsoleMsg("you're too far in now. enjoy 173 :3")
					EndIf
				Else If Infect3312 > 60 And temp =< 60.0
					Msg = stringTempThing2
					MsgTimer = 70*6
					PlaySound_Strict(HorrorSFX(5))
				Else If Infect3312 > 80 And temp =< 80.0
					MsgTimer = 70*6
					PlaySound_Strict(HorrorSFX(15))
				End If
				If Infect3312 > 80 Then
					Crouch = True
					If InfectOverride3312=0 
						If Rand(0,5) = 5
							stringTemp$ = stringTempThingA
							For i = 0 To Rand(10,15)
								stringTemp$ = Replace(stringTempThingA,Mid(stringTempThingA,Rand(1,Len(stringTemp)-1),1),Chr(Rand(130,250)))
							Next
						Else
							stringTemp$ = stringTempThingB
							For i = 0 To Rand(10,15)
								stringTemp$ = Replace(stringTempThingB,Mid(stringTempThingB,Rand(1,Len(stringTemp)-1),1),Chr(Rand(130,250)))
							Next
						End If
						Msg = stringTemp
					EndIf
				EndIf
			Else If InfectType3312=1
				If Infect3312 > 1.01 And temp =< 1.01 Then
					Msg = "You glance over the drawing on the document."
					MsgTimer = 70*6
				Else If Infect3312 > 10 And temp =< 10.0
					Msg = "The drawing is starting to interest you."
					MsgTimer = 70*6
					PlaySound_Strict(HorrorSFX(11))
				Else If Infect3312 > 20 And temp =< 20.0
					Msg = "You can't stop thinking about that drawing..."
					MsgTimer = 70*6
					PlaySound_Strict(HorrorSFX(6))
				Else If Infect3312 > 40 And temp =< 40.0
					Msg = "You are begining to experience difficulty thinking straight..."
					MsgTimer = 70*6
					PlaySound_Strict(HorrorSFX(0))
					If (Not furri) Then ;DOCUMENT EDITION
						HideEntity Curr173\obj
						HideEntity Curr173\obj3
						Local temp3# = EntityScaleX(Curr173\obj)
						Curr173\obj = LoadAnimMesh_Strict("GFX\npcs\173bodyOwO.b3d")
						Curr173\obj3 = LoadAnimMesh_Strict("GFX\npcs\173headOwO.b3d")
						Animate Curr173\obj, 1, 0.05
						ScaleEntity Curr173\obj, temp3,temp3,temp3
						ScaleEntity Curr173\obj3, temp3,temp3,temp3
						ShowEntity Curr173\obj
						ShowEntity Curr173\obj3
						
						Curr173\Speed = (GetINIFloat("DATA\NPCs.ini", "SCP-173", "speed") / 100.0)
						If halloween990 Then
							If (Not HalloweenTex) Then
								tex970reload = LoadTexture_Strict("GFX\npcs\173h.pt", 1) ;dont know what the fuck b3d is on about but it thinks this was defined twice
								EntityTexture Curr173\obj, tex970reload, 0, 0
								EntityTexture Curr173\obj3, tex970reload, 0, 0
								FreeTexture tex970reload
							EndIf
						EndIf
						furri = True
						CreateConsoleMsg("your too far in now. enjoy 173 :3")
					EndIf
				Else If Infect3312 > 60 And temp =< 60.0
					Msg = stringTempThing2
					MsgTimer = 70*6
					PlaySound_Strict(HorrorSFX(5))
				Else If Infect3312 > 80 And temp =< 80.0
					MsgTimer = 70*6
					PlaySound_Strict(HorrorSFX(15))
				End If
				If Infect3312 > 80 Then
					Crouch = True
					If InfectOverride3312=0 
						If Rand(0,5) = 5
							stringTemp$ = stringTempThingA
							For i = 0 To Rand(10,15)
								stringTemp$ = Replace(stringTempThingA,Mid(stringTempThingA,Rand(1,Len(stringTemp)-1),1),Chr(Rand(130,250)))
							Next
						Else
							stringTemp$ = stringTempThingB
							For i = 0 To Rand(10,15)
								stringTemp$ = Replace(stringTempThingB,Mid(stringTempThingB,Rand(1,Len(stringTemp)-1),1),Chr(Rand(130,250)))
							Next
						End If
						Msg = stringTemp
					EndIf
				EndIf
			EndIf
		Else
			DeathMSG = "Subject D-9341 found sitting on the floor of the [REDACTED] "
			DeathMSG = DeathMSG + "room of Site-[REDACTED]. Upon discovery, Subject D-9341 proceeded to "
			DeathMSG = DeathMSG + "[DATA EXPUNGED] several MTF units before MTF-[REDACTED] [DATA EXPUNGED] the subject. It is currently "
			DeathMSG = DeathMSG + "unknown the cause of D-9341's behaviour, will continue to study."
			Kill() : KillAnim = 1
		End If
	EndIf
	EndIf
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D