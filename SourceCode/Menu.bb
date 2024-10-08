Global MenuBack% = LoadImage_Strict("GFX\menu\back.jpg")
Global MenuText% = LoadImage_Strict("GFX\menu\scptext.jpg")
Global Menu173% = LoadImage_Strict("GFX\menu\173back.jpg")
MenuWhite = LoadImage_Strict("GFX\menu\menuwhite.jpg")
MenuBlack = LoadImage_Strict("GFX\menu\menublack.jpg")
MaskImage MenuBlack, 255,255,0
Global QuickLoadIcon% = LoadImage_Strict("GFX\menu\QuickLoading.png")

Global CurrentLoadingPercent% = 0

Global LoadingFast% = False

Const FRAME_THICK = 3

ResizeImage(MenuBack, ImageWidth(MenuBack) * MenuScale, ImageHeight(MenuBack) * MenuScale)
ResizeImage(MenuText, ImageWidth(MenuText) * MenuScale, ImageHeight(MenuText) * MenuScale)
ResizeImage(Menu173, ImageWidth(Menu173) * MenuScale, ImageHeight(Menu173) * MenuScale)
ResizeImage(QuickLoadIcon, ImageWidth(QuickLoadIcon) * MenuScale, ImageHeight(QuickLoadIcon) * MenuScale)

For i = 0 To 3
	ArrowIMG(i) = LoadImage_Strict("GFX\menu\arrow.png")
	RotateImage(ArrowIMG(i), 90 * i)
	HandleImage(ArrowIMG(i), 0, 0)
Next

Global IS_3DMENU_ENABLED% = GetINIInt(OptionFile, "options", "3dMenu")

Global IS_3DMENU_ENABLED_PREV% = IS_3DMENU_ENABLED

Global LOCKED3D%

Global LOCKEDAA%

Include "SourceCode\3D Menu.bb"

Global RandomSeed$

Global changelogLoaded%

Dim MenuBlinkTimer%(2), MenuBlinkDuration%(2)
MenuBlinkTimer%(0) = 1
MenuBlinkTimer%(1) = 1

Global MenuStr$, MenuStrX%, MenuStrY%

Global MainMenuTab%

Global IntroEnabled% = GetINIInt(OptionFile, "options", "intro enabled")

Global SelectedInputBox%

Global SavePath$ = GetEnv("localappdata")+"\DangerBreach\Saves\"
Global SaveMSG$

;nykyisen tallennuksen nimi ja samalla missä kansiossa tallennustiedosto sijaitsee saves-kansiossa
Global CurrSave$

Global NoCursor% = GetINIInt(OptionFile, "options", "no cursor")

Global SaveGameAmount%
Dim SaveGames$(SaveGameAmount+1) 
Dim SaveGameTime$(SaveGameAmount + 1)
Dim SaveGameDate$(SaveGameAmount + 1)
Dim SaveGameVersion$(SaveGameAmount + 1)
Dim SaveGameDataVersion#(SaveGameAmount + 1)

Global SavedMapsAmount% = 0
Dim SavedMaps$(SavedMapsAmount+1)
Dim SavedMapsAuthor$(SavedMapsAmount+1)

Global SelectedMap$

LoadSaveGames()

Global CurrLoadGamePage% = 0

Global TitleHappened% = 0

Global LoadStarted% = 0

Global versionTextTemp$ = ""

Global isUpgrade% = 0

If IS_3DMENU_ENABLED Then Init3DMenu()

Function UpdateMainMenu()
	CatchErrors("Uncaught (UpdateMainMenu)")
	Local x%, y%, width%, height%, temp%
	
	TitleHappened = 1
	BlitzcordGameStatus=1
	UpdateBlitzcord()
	
	;SyncGame()
	
	DeleteMenuGadgets()
	
	Color 0,0,0
	Rect 0,0,RealGraphicWidth,RealGraphicHeight,True
	If NoCursor = True Then
		ShowPointer()
	EndIf
	
	If Not IS_3DMENU_ENABLED Then ;3D Menu is disabled
		
		DrawImage(MenuBack, 0, 0)
		
		If (MilliSecs2() Mod MenuBlinkTimer(0)) >= Rand(MenuBlinkDuration(0)) Then
			DrawImage(Menu173, RealGraphicWidth - ImageWidth(Menu173), RealGraphicHeight - ImageHeight(Menu173))
		EndIf
	Else
		Update3DMenu()
	EndIf
	
	If Rand(300) = 1 Then
		MenuBlinkTimer(0) = Rand(4000, 8000)
		MenuBlinkDuration(0) = Rand(200, 500)
	End If
	
	AASetFont Font1
	
	MenuBlinkTimer(1)=MenuBlinkTimer(1)-FPSfactor
	If MenuBlinkTimer(1) < MenuBlinkDuration(1) Then
		Color(50, 50, 50)
		AAText(MenuStrX + Rand(-5, 5), MenuStrY + Rand(-5, 5), MenuStr, True)
		If MenuBlinkTimer(1) < 0 Then
			MenuBlinkTimer(1) = Rand(700, 800)
			MenuBlinkDuration(1) = Rand(10, 35)
			MenuStrX = Rand(700, 1000) * MenuScale
			MenuStrY = Rand(100, 600) * MenuScale
			
			Select Rand(0, 22)
				Case 0, 2, 3
					MenuStr = "DON'T BLINK"
				Case 4, 5
					MenuStr = "Secure. Contain. Protect."
				Case 6, 7, 8
					MenuStr = "You want happy endings? Fuck you."
				Case 9, 10, 11
					MenuStr = "Sometimes we would have had time to scream."
				Case 12, 19
					MenuStr = "NIL"
				Case 13
					MenuStr = "NO"
				Case 14
					MenuStr = "black white black white black white gray"
				Case 15
					MenuStr = "Stone does not care"
				Case 16
					MenuStr = "9341"
				Case 17
					MenuStr = "It controls the doors"
				Case 18
					MenuStr = "e8m106]af173o+079m895w914"
				Case 20
					MenuStr = "It has taken over everything"
				Case 21
					MenuStr = "The spiral is growing"
				Case 22
					MenuStr = Chr(34)+"Some kind of gestalt effect due to massive reality damage."+Chr(34)
			End Select
		EndIf
	EndIf
	
	AASetFont Font2
	
	DrawImage(MenuText, RealGraphicWidth / 2 - ImageWidth(MenuText) / 2, RealGraphicHeight - 20 * MenuScale - ImageHeight(MenuText))
	
	If (GraphicWidth > 1240 * MenuScale) And (Not IS_3DMENU_ENABLED) Then
		DrawTiledImageRect(MenuWhite, 0, 5, 512, 7 * MenuScale, 985.0 * MenuScale, 407.0 * MenuScale, (RealGraphicWidth - 1240 * MenuScale) + 300, 7 * MenuScale)
	EndIf
	
	If (Not MouseDown1)
		OnSliderID = 0
	EndIf
	
	If MainMenuTab = 0 Then
		For i% = 0 To 4
			temp = False
			x = 159 * MenuScale
			y = (286 + 100 * i) * MenuScale
			
			width = 400 * MenuScale
			height = 70 * MenuScale
			
			temp = (MouseHit1 And MouseOn(x, y, width, height))
			
			Local txt$
			Select i
				Case 0
					If (Not MemeMode) Then txt = "NEW GAME"
					If (MemeMode) Then txt = "CONTINUE SUFFERING"
					RandomSeed = ""
					If temp Then 
						If Rand(15)=1 Then 
							If (Not MemeMode)
								Select Rand(16)
									Case 1 
										RandomSeed = "NIL"
									Case 2
										RandomSeed = "NO"
									Case 3
										RandomSeed = "d9341"
									Case 4
										RandomSeed = "5CP_173"
									Case 5
										RandomSeed = "DONTBLINK"
									Case 6
										RandomSeed = "CRUNCH"
									Case 7
										RandomSeed = "die"
									Case 8
										RandomSeed = "HTAED"
									Case 9
										RandomSeed = "rustledjim"
									Case 10
										RandomSeed = "larry"
									Case 11
										RandomSeed = "JORGE"
									Case 12
										RandomSeed = "dirtymetal"
									Case 13
										RandomSeed = "whatpumpkin"
									Case 14
										RandomSeed = "FUNNIMAN_EXE"
									Case 15
										RandomSeed = "NULL"
									Case 16
										RandomSeed = "SPIRAL"
								End Select
							Else
								Select Rand(9)
									Case 1 
										RandomSeed = "what"
									Case 2
										RandomSeed = "baller"
									Case 3
										RandomSeed = "barrel"
									Case 4
										RandomSeed = "baldi"
									Case 5
										RandomSeed = "mrbeast"
									Case 6
										RandomSeed = "diehard"
									Case 7
										RandomSeed = "jorge"
									Case 8
										RandomSeed = "peanut"
									Case 9
										RandomSeed = "whatspiral"
								End Select
							EndIf
						Else
							n = Rand(4,10)
							For i = 1 To n
								If Rand(3)=1 Then
									RandomSeed = RandomSeed + Rand(0,9)
								Else
									RandomSeed = RandomSeed + Chr(Rand(97,122))
								EndIf
							Next							
						EndIf
						
						;RandomSeed = MilliSecs()
						MainMenuTab = 1
					EndIf
				Case 1
					If (Not MemeMode) Then txt = "LOAD GAME"
					If (MemeMode) Then txt = "LOAD SHITTY SAVE"
					If temp Then
						LoadSaveGames()
						MainMenuTab = 2
					EndIf
				Case 2
					If (Not MemeMode) Then txt = "OPTIONS"
					If (MemeMode) Then txt = "NOTHING USEFUL"
					If temp Then MainMenuTab = 3
				Case 3
					txt = "CHANGELOG"
					If temp Then MainMenuTab = 8
					changelogLoaded = False
				Case 4
					If (Not MemeMode) Then txt = "QUIT"
					If (MemeMode) Then txt = "END SUFFERING"
					If temp Then
						;DeInitExt
						;alDestroy()
						;FMOD_Pause(MusicCHN)
						;FMOD_CloseStream(CurrMusicStream)
						;FMOD_Close()
						;FMOD_StopStream(CurrMusicStream)
						FSOUND_Stream_Stop(CurrMusicStream)
						;FSOUND_Close()
						End
					EndIf
			End Select
			
			DrawButton(x, y, width, height, txt)
			
			;rect(x + 4, y + 4, width - 8, height - 8)
			;color 255, 255, 255	
			;text(x + width / 2, y + height / 2, Str, True, True)
		Next	
		
	Else
		
		x = 159 * MenuScale
		y = 286 * MenuScale
		
		width = 400 * MenuScale
		height = 70 * MenuScale
		
		DrawFrame(x, y, width, height)
		
		If DrawButton(x + width + 20 * MenuScale, y, 580 * MenuScale - width - 20 * MenuScale, height, "BACK", False) Then 
			Select MainMenuTab
				Case 1
					PutINIValue(OptionFile, "options", "intro enabled", IntroEnabled%)
					MainMenuTab = 0
				Case 2
					CurrLoadGamePage = 0
					MainMenuTab = 0
				Case 3,5,6,7,9 ;save the options
					SaveOptionsINI()
					
					UserTrackCheck% = 0
					UserTrackCheck2% = 0
					
					AntiAlias Opt_AntiAlias
					MainMenuTab = 0
				Case 4 ;move back to the "new game" tab
					MainMenuTab = 1
					CurrLoadGamePage = 0
					MouseHit1 = False
				Default
					MainMenuTab = 0
			End Select
		EndIf
		
		Select MainMenuTab
			Case 1 ; New game
				;[Block]
				
				x = 159 * MenuScale
				y = 286 * MenuScale
				
				width = 400 * MenuScale
				height = 70 * MenuScale
				
				Color(255, 255, 255)
				AASetFont Font2
				If (Not MemeMode) Then AAText(x + width / 2, y + height / 2, "NEW GAME", True, True)
				If (MemeMode) Then AAText(x + width / 2, y + height / 2, "CHOOSE KETER YOU COWARD", True, True)
				
				x = 160 * MenuScale
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				height = 330 * MenuScale
				
				DrawFrame(x, y, width, height)				
				
				AASetFont Font1
				
				AAText (x + 20 * MenuScale, y + 20 * MenuScale, "Name:")
				CurrSave = InputBox(x + 150 * MenuScale, y + 15 * MenuScale, 200 * MenuScale, 30 * MenuScale, CurrSave, 1)
				CurrSave = Left(CurrSave, 15)
				CurrSave = Replace(CurrSave,":","")
				CurrSave = Replace(CurrSave,".","")
				CurrSave = Replace(CurrSave,"/","")
				CurrSave = Replace(CurrSave,"\","")
				CurrSave = Replace(CurrSave,"<","")
				CurrSave = Replace(CurrSave,">","")
				CurrSave = Replace(CurrSave,"|","")
				CurrSave = Replace(CurrSave,"?","")
				CurrSave = Replace(CurrSave,Chr(34),"")
				CurrSave = Replace(CurrSave,"*","")
				
				Color 255,255,255
				If SelectedMap = "" Then
					AAText (x + 20 * MenuScale, y + 60 * MenuScale, "Map seed:")
					RandomSeed = Left(InputBox(x+150*MenuScale, y+55*MenuScale, 200*MenuScale, 30*MenuScale, RandomSeed, 3),15)	
				Else
					AAText (x + 20 * MenuScale, y + 60 * MenuScale, "Selected map:")
					Color (255, 255, 255)
					Rect(x+150*MenuScale, y+55*MenuScale, 200*MenuScale, 30*MenuScale)
					Color (0, 0, 0)
					Rect(x+150*MenuScale+2, y+55*MenuScale+2, 200*MenuScale-4, 30*MenuScale-4)
					
					Color (255, 0,0)
					If Len(SelectedMap)>15 Then
						AAText(x+150*MenuScale + 100*MenuScale, y+55*MenuScale + 15*MenuScale, Left(SelectedMap,14)+"...", True, True)
					Else
						AAText(x+150*MenuScale + 100*MenuScale, y+55*MenuScale + 15*MenuScale, SelectedMap, True, True)
					EndIf
					
					If DrawButton(x+370*MenuScale, y+55*MenuScale, 120*MenuScale, 30*MenuScale, "Deselect", False) Then
						SelectedMap=""
					EndIf
				EndIf
				
				AAText(x + 20 * MenuScale, y + 110 * MenuScale, "Enable intro sequence:")
				IntroEnabled = DrawTick(x + 280 * MenuScale, y + 110 * MenuScale, IntroEnabled)	
				
				;Local modeName$, modeDescription$, selectedDescription$
				AAText (x + 20 * MenuScale, y + 150 * MenuScale, "Difficulty:")				
				For i = SAFE To CUSTOM
					If DrawTick(x + 20 * MenuScale, y + (180+30*i) * MenuScale, (SelectedDifficulty = difficulties(i))) Then SelectedDifficulty = difficulties(i)
					Color(difficulties(i)\r,difficulties(i)\g,difficulties(i)\b)
					AAText(x + 60 * MenuScale, y + (180+30*i) * MenuScale, difficulties(i)\name)
				Next
				
				Color(255, 255, 255)
				DrawFrame(x + 150 * MenuScale,y + 155 * MenuScale, 410*MenuScale, 150*MenuScale)
				
				If SelectedDifficulty\customizable Then
					SelectedDifficulty\permaDeath =  DrawTick(x + 160 * MenuScale, y + 165 * MenuScale, (SelectedDifficulty\permaDeath))
					AAText(x + 200 * MenuScale, y + 165 * MenuScale, "Permadeath")
					
					If DrawTick(x + 160 * MenuScale, y + 195 * MenuScale, SelectedDifficulty\saveType = SAVEANYWHERE And (Not SelectedDifficulty\permaDeath), SelectedDifficulty\permaDeath) Then 
						SelectedDifficulty\saveType = SAVEANYWHERE
					Else
						SelectedDifficulty\saveType = SAVEONSCREENS
					EndIf
					
					AAText(x + 200 * MenuScale, y + 195 * MenuScale, "Save anywhere")	
					
					SelectedDifficulty\aggressiveNPCs =  DrawTick(x + 160 * MenuScale, y + 225 * MenuScale, SelectedDifficulty\aggressiveNPCs)
					AAText(x + 200 * MenuScale, y + 225 * MenuScale, "Aggressive NPCs")
					
					;Other factor's difficulty
					Color 255,255,255
					DrawImage ArrowIMG(1),x + 155 * MenuScale, y+251*MenuScale
					If MouseHit1
						If ImageRectOverlap(ArrowIMG(1),x + 155 * MenuScale, y+251*MenuScale, ScaledMouseX(),ScaledMouseY(),0,0)
							If SelectedDifficulty\otherFactors < HARD
								SelectedDifficulty\otherFactors = SelectedDifficulty\otherFactors + 1
							Else
								SelectedDifficulty\otherFactors = EASY
							EndIf
							PlaySound_Strict(ButtonSFX)
						EndIf
					EndIf
					Color 255,255,255
					Select SelectedDifficulty\otherFactors
						Case EASY
							AAText(x + 200 * MenuScale, y + 255 * MenuScale, "Other difficulty factors: Easy")
						Case NORMAL
							AAText(x + 200 * MenuScale, y + 255 * MenuScale, "Other difficulty factors: Normal")
						Case HARD
							AAText(x + 200 * MenuScale, y + 255 * MenuScale, "Other difficulty factors: Hard")
					End Select
				Else
					RowText(SelectedDifficulty\description, x+160*MenuScale, y+160*MenuScale, (410-20)*MenuScale, 200)					
				EndIf
				
				If DrawButton(x, y + height + 20 * MenuScale, 160 * MenuScale, 70 * MenuScale, "Load map", False) Then
					MainMenuTab = 4
					LoadSavedMaps()
				EndIf
				
				AASetFont Font2
				
				If DrawButton(x + 420 * MenuScale, y + height + 20 * MenuScale, 160 * MenuScale, 70 * MenuScale, "START", False) Then
					If CurrSave = "" Then CurrSave = "Untitled Save"
					
					If RandomSeed = "" Then
						RandomSeed = Abs(MilliSecs())
					EndIf
					
					SeedRnd GenerateSeedNumber(RandomSeed)
					
					Local samefound% = 0
					
					For i% = 1 To SaveGameAmount
						If (samefound = 0 And SaveGames(i - 1) = CurrSave) Or (samefound > 0 And SaveGames(i - 1) = CurrSave + " (" + (samefound + 1) + ")") Then
							samefound = samefound + 1
							i = 0
						EndIf
					Next
						
					If samefound > 0 Then CurrSave = CurrSave + " (" + (samefound + 1) + ")"
					
					DeleteMenuGadgets()
					
					LoadEntities()
					InitNewGame()
					MainMenuOpen = False
					FlushKeys()
					FlushMouse()
					
					PutINIValue(OptionFile, "options", "intro enabled", IntroEnabled%)
					
				EndIf
				
				;[End Block]
			Case 2 ;load game
				;[Block]
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				;height = 300 * MenuScale
				height = 510 * MenuScale
				
				DrawFrame(x, y, width, height)
				
				x = 159 * MenuScale
				y = 286 * MenuScale
				
				width = 400 * MenuScale
				height = 70 * MenuScale
				
				Color(255, 255, 255)
				AASetFont Font2
				If (Not MemeMode) Then AAText(x + width / 2, y + height / 2, "LOAD GAME", True, True)
				If (MemeMode) Then AAText(x + width / 2, y + height / 2, "COWARD", True, True)
				
				x = 160 * MenuScale
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				height = 296 * MenuScale
				
				;AASetFont Font1	
				
				AASetFont Font2
				
				If CurrLoadGamePage < Ceil(Float(SaveGameAmount)/6.0)-1 And SaveMSG = "" Then 
					If DrawButton(x+530*MenuScale, y + 510*MenuScale, 50*MenuScale, 55*MenuScale, ">") Then
						CurrLoadGamePage = CurrLoadGamePage+1
					EndIf
				Else
					DrawFrame(x+530*MenuScale, y + 510*MenuScale, 50*MenuScale, 55*MenuScale)
					Color(100, 100, 100)
					AAText(x+555*MenuScale, y + 537.5*MenuScale, ">", True, True)
				EndIf
				If CurrLoadGamePage > 0 And SaveMSG = "" Then
					If DrawButton(x, y + 510*MenuScale, 50*MenuScale, 55*MenuScale, "<") Then
						CurrLoadGamePage = CurrLoadGamePage-1
					EndIf
				Else
					DrawFrame(x, y + 510*MenuScale, 50*MenuScale, 55*MenuScale)
					Color(100, 100, 100)
					AAText(x+25*MenuScale, y + 537.5*MenuScale, "<", True, True)
				EndIf
				
				DrawFrame(x+50*MenuScale,y+510*MenuScale,width-100*MenuScale,55*MenuScale)
				
				AAText(x+(width/2.0),y+536*MenuScale,"Page "+Int(Max((CurrLoadGamePage+1),1))+"/"+Int(Max((Int(Ceil(Float(SaveGameAmount)/6.0))),1)),True,True)
				
				AASetFont Font1
				
				If CurrLoadGamePage > Ceil(Float(SaveGameAmount)/6.0)-1 Then
					CurrLoadGamePage = CurrLoadGamePage - 1
				EndIf
				
				If SaveGameAmount = 0 Then
					AAText (x + 20 * MenuScale, y + 20 * MenuScale, "No saved games.")
				Else
					x = x + 20 * MenuScale
					y = y + 20 * MenuScale
					
					For i% = (1+(6*CurrLoadGamePage)) To 6+(6*CurrLoadGamePage)
						If i <= SaveGameAmount Then
							DrawFrame(x,y,540* MenuScale, 70* MenuScale)
							
							If Not (SaveGameVersion(i - 1) = "2.3.2" Or SaveGameVersion(i - 1) = "2.3.1" Or SaveGameVersion(i - 1) = "2.3.0")
								If Not (SaveGameVersion(i - 1) = "2.2.9" Or SaveGameVersion(i - 1) = "2.2.8" Or SaveGameVersion(i - 1) = "2.2.7")
									If Not (SaveGameVersion(i - 1) = "2.2.6" Or SaveGameVersion(i - 1) = "2.2.5" Or SaveGameVersion(i - 1) = "2.2.4")
										If Not (SaveGameVersion(i - 1) = "2.2.3" Or SaveGameVersion(i - 1) = "2.2.2" Or SaveGameVersion(i - 1) = "2.2.1")
											If Not (SaveGameDataVersion(i - 1) > SavFormatVersionNumber Or SaveGameDataVersion(i - 1) < SavFormatVersionNumber)
												Color 255,255,255
												AAText(x + 20 * MenuScale, y + (10+36) * MenuScale, "Saved in: " + SaveGameVersion(i - 1) + " Save Fmt: " + SaveGameDataVersion(i - 1))
											Else
												Color 255,0,0
												AAText(x + 20 * MenuScale, y + (10+36) * MenuScale, "Saved in: " + SaveGameVersion(i - 1) + " Save Fmt: Pre-2.0")
											EndIf
										Else
											Color 255,0,0
											AAText(x + 20 * MenuScale, y + (10+36) * MenuScale, "Saved in: " + SaveGameVersion(i - 1) + " Save Fmt: Pre-2.0")
										EndIf
									Else
										Color 255,0,0
										AAText(x + 20 * MenuScale, y + (10+36) * MenuScale, "Saved in: " + SaveGameVersion(i - 1) + " Save Fmt: Pre-2.0")
									EndIf
								Else
									Color 255,0,0
									AAText(x + 20 * MenuScale, y + (10+36) * MenuScale, "Saved in: " + SaveGameVersion(i - 1) + " Save Fmt: Pre-2.0")
								EndIf
							Else
								Color 255,0,0
								AAText(x + 20 * MenuScale, y + (10+36) * MenuScale, "Saved in: " + SaveGameVersion(i - 1) + " Save Fmt: Pre-2.0")
							EndIf
							
							AAText(x + 20 * MenuScale, y + 10 * MenuScale, SaveGames(i - 1))
							AAText(x + 20 * MenuScale, y + (10+18) * MenuScale, SaveGameTime(i - 1)) ;y + (10+23) * MenuScale
							AAText(x + 120 * MenuScale, y + (10+18) * MenuScale, SaveGameDate(i - 1))
							
							If SaveMSG = "" Then
								If Not (SaveGameVersion(i - 1) = "2.3.1" Or SaveGameVersion(i - 1) = "2.3.0" Or SaveGameVersion(i - 1) = "2.2.9" Or SaveGameVersion(i - 1) = "2.2.8" Or SaveGameVersion(i - 1) = "2.2.7" Or SaveGameVersion(i - 1) = "2.2.6" Or SaveGameVersion(i - 1) = "2.2.5" Or SaveGameVersion(i - 1) = "2.2.4" Or SaveGameVersion(i - 1) = "2.2.3" Or SaveGameVersion(i - 1) = "2.2.2" Or SaveGameVersion(i - 1) = "2.2.1") Then
									If SaveGameDataVersion(i - 1) > SavFormatVersionNumber Then
										DrawFrame(x + 280 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale)
										Color(255, 0, 0)
										AAText(x + 330 * MenuScale, y + 34 * MenuScale, "Load", True, True)
									Else
										If SaveGameDataVersion(i - 1) < SavFormatVersionNumber Then
											If DrawButton(x + 280 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, "Upgrade", False) Then
												isUpgrade = 1
												DebugLog isUpgrade
												SaveMSG = SaveGames(i - 1)
												DebugLog SaveMSG
												Exit
											EndIf
										Else
											If DrawButton(x + 280 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, "Load", False) Then
												isLoadedSave = True
												LoadEntities()
												LoadAllSounds()
												LoadGame(SavePath + SaveGames(i - 1) + "\")
												CurrSave = SaveGames(i - 1)
												DeleteMenuGadgets()
												InitLoadGame()
												isLoadedSave = False
												MainMenuOpen = False
											EndIf
										EndIf
									EndIf
								Else
									DrawFrame(x + 280 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale)
									Color(255, 0, 0)
									AAText(x + 330 * MenuScale, y + 34 * MenuScale, "Load", True, True)
								EndIf
								
								If DrawButton(x + 400 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, "Delete", False) Then
									isUpgrade = 0
									DebugLog isUpgrade
									SaveMSG = SaveGames(i - 1)
									DebugLog SaveMSG
									Exit
								EndIf
							Else
								DrawFrame(x + 280 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale)
								If SaveGameDataVersion(i - 1) > SavFormatVersionNumber Or SaveGameDataVersion(i - 1) < SavFormatVersionNumber Or SaveGameVersion(i - 1) = "2.3.1" Or SaveGameVersion(i - 1) = "2.3.0" Or SaveGameVersion(i - 1) = "2.2.9" Or SaveGameVersion(i - 1) = "2.2.8" Or SaveGameVersion(i - 1) = "2.2.7" Or SaveGameVersion(i - 1) = "2.2.6" Or SaveGameVersion(i - 1) = "2.2.5" Or SaveGameVersion(i - 1) = "2.2.4" Or SaveGameVersion(i - 1) = "2.2.3" Or SaveGameVersion(i - 1) = "2.2.2" Or SaveGameVersion(i - 1) = "2.2.1" Then
									If SaveGameVersion(i - 1) = "2.3.2" Or SaveGameDataVersion(i - 1) < SavFormatVersionNumber Then
										Color(100, 100, 100)
										AAText(x + 330 * MenuScale, y + 34 * MenuScale, "Upgrade", True, True)
									Else
										Color(255, 0, 0)
										AAText(x + 330 * MenuScale, y + 34 * MenuScale, "Load", True, True)
									EndIf
								Else
									Color(100, 100, 100)
									AAText(x + 330 * MenuScale, y + 34 * MenuScale, "Load", True, True)
								EndIf
								
								DrawFrame(x + 400 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale)
								Color(100, 100, 100)
								AAText(x + 450 * MenuScale, y + 34 * MenuScale, "Delete", True, True)
							EndIf
							
							y = y + 80 * MenuScale
						Else
							Exit
						EndIf
					Next
					
					If SaveMSG <> ""
						x = 740 * MenuScale
						y = 376 * MenuScale
						DrawFrame(x, y, 420 * MenuScale, 200 * MenuScale)
						If isUpgrade = 1 Then
							RowText("Are you sure you want to upgrade this save?", x + 15 * MenuScale, y + 15 * MenuScale, 400 * MenuScale, 200 * MenuScale)
							;AAText(x + 20 * MenuScale, y + 15 * MenuScale, "Are you sure you want to upgrade this save?")
							If Not FileSize(SavePath + SaveMSG + "\save.txt") = 0 Then
								DrawFrame(x + 50 * MenuScale, y + 150 * MenuScale, 100 * MenuScale, 30 * MenuScale)
								Color(100, 100, 100)
								AAText(x + 50 * MenuScale, y + 150 * MenuScale, "Yes", True, True)
							Else
								If DrawButton(x + 50 * MenuScale, y + 150 * MenuScale, 100 * MenuScale, 30 * MenuScale, "Yes", False) Then
									Local f%
									Local f2%
									Local localVer$
									CreateDir(SavePath + SaveMSG + " (BACKUP)")
									CopyFile(SavePath + SaveMSG + "\Data.dbsav", SavePath + SaveMSG + " (BACKUP)\Data.dbsav")
									f = WriteFile(SavePath + SaveMSG + "\Data.dbsav")
									f2 = ReadFile(SavePath + SaveMSG + " (BACKUP)\Data.dbsav")
									WriteString f, CurrentTime()
									WriteString f, CurrentDate()
									ReadString(f2)
									ReadString(f2)
									WriteInt(f, ReadInt(f2))
									For j = 0 To 5
										WriteFloat(f, ReadFloat(f2))
									Next
									WriteString(f, ReadString(f2))
									For j = 0 To 1
										WriteFloat(f, ReadFloat(f2))
									Next
									
									localVer = ReadString(f2)
									WriteString f, VersionNumber
									WriteFloat f, SavFormatVersionNumber
									
									For j = 0 To 2
										WriteFloat(f, ReadFloat(f2))
									Next
									WriteInt(f, ReadInt(f2))
									WriteInt(f, ReadInt(f2))
									WriteFloat(f, ReadFloat(f2))
									WriteByte(f, ReadByte(f2))
									For j = 0 To 8
										WriteFloat(f, ReadFloat(f2))
									Next
									WriteString(f, ReadString(f2))
									For j = 0 To 6
										WriteFloat(f, ReadFloat(f2))
									Next
									WriteByte(f, ReadByte(f2))
									For j = 0 To 4
										WriteFloat(f, ReadFloat(f2))
									Next
									
									; PUT ALL NEW SAVE STUFF HERE
									;----------------------------
									; basically: use if statements to check the game version, then use that to apply upgrades
									; ex:
									; If (Not localVer = "4.2.0")
									;     WriteFloat(f, someSaveParamAddedIn4.2.0)
									;     If (Not localVer = "4.1.9")
									;         WriteFloat(f, someSaveParamAddedIn4.1.9)
									;     EndIf
									; EndIf
									;----------------------------
									
									
									
									;----------------------------
									; after we've upgraded the save file, it's time to continue with writing the original save file back
									;----------------------------
									; BACK TO NON NEW STUFF NOW
									
									While Not Eof(f2)
										WriteByte(f, ReadByte(f2))
									Wend
									
									CloseFile f
									CloseFile f2
									
									;isUpgrade = 0
									SaveMSG = ""
									LoadSaveGames()
								EndIf
							EndIf
							If DrawButton(x + 250 * MenuScale, y + 150 * MenuScale, 100 * MenuScale, 30 * MenuScale, "No", False) Then
								;isUpgrade = 0
								SaveMSG = ""
							EndIf
						Else
							RowText("Are you sure you want to delete this save?", x + 15 * MenuScale, y + 15 * MenuScale, 400 * MenuScale, 200 * MenuScale)
							;AAText(x + 20 * MenuScale, y + 15 * MenuScale, "Are you sure you want to delete this save?")
							If DrawButton(x + 50 * MenuScale, y + 150 * MenuScale, 100 * MenuScale, 30 * MenuScale, "Yes", False) Then
								If FileSize(SavePath + SaveMSG + "\save.txt") = 0 Then
									DeleteFile(SavePath + SaveMSG + "\Data.dbsav")
								Else
									DeleteFile(SavePath + SaveMSG + "\save.txt")
								EndIf
								DeleteDir(SavePath + SaveMSG)
								;isUpgrade = 0
								SaveMSG = ""
								LoadSaveGames()
							EndIf
							If DrawButton(x + 250 * MenuScale, y + 150 * MenuScale, 100 * MenuScale, 30 * MenuScale, "No", False) Then
								;isUpgrade = 0
								SaveMSG = ""
							EndIf
						EndIf
					EndIf
				EndIf
				
				
				
				;[End Block]
			Case 3,5,6,7,9 ;options
				;[Block]
				
				x = 159 * MenuScale
				y = 286 * MenuScale
				
				width = 400 * MenuScale
				height = 70 * MenuScale
				
				Color(255, 255, 255)
				AASetFont Font2
				If (Not MemeMode) Then AAText(x + width / 2, y + height / 2, "OPTIONS", True, True)
				If (MemeMode) Then AAText(x + width / 2, y + height / 2, "OOPCHEENS", True, True)
				
				x = 160 * MenuScale
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				height = 60 * MenuScale
				DrawFrame(x, y, width, height+30*MenuScale)
				
				Color 0,255,0
				If MainMenuTab = 3
					Rect(x+15*MenuScale,y+5*MenuScale,(width/5)+10*MenuScale,(height/2)+10*MenuScale,True)
				ElseIf MainMenuTab = 5
					Rect(x+155*MenuScale,y+5*MenuScale,(width/5)+10*MenuScale,(height/2)+10*MenuScale,True)
				ElseIf MainMenuTab = 6
					Rect(x+295*MenuScale,y+5*MenuScale,(width/5)+10*MenuScale,(height/2)+10*MenuScale,True)
				ElseIf MainMenuTab = 7
					Rect(x+435*MenuScale,y+5*MenuScale,(width/5)+10*MenuScale,(height/2)+10*MenuScale,True)
				ElseIf MainMenuTab = 9
					Rect(x+15*MenuScale,y+30*MenuScale,(width/5)+10*MenuScale,(height/2)+10*MenuScale,True)
				EndIf
				
				Color 255,255,255
				If (Not MemeMode)
					If DrawButton(x+20*MenuScale,y+10*MenuScale,width/5,height/2, "GRAPHICS", False) Then MainMenuTab = 3
					If DrawButton(x+160*MenuScale,y+10*MenuScale,width/5,height/2, "AUDIO", False) Then MainMenuTab = 5
					If DrawButton(x+300*MenuScale,y+10*MenuScale,width/5,height/2, "CONTROLS", False) Then MainMenuTab = 6
					If DrawButton(x+440*MenuScale,y+10*MenuScale,width/5,height/2, "ADVANCED", False) Then MainMenuTab = 7
					If DrawButton(x+20*MenuScale,y+35*MenuScale,width/5,height/2, "MEME MODE", False) Then MainMenuTab = 9
				Else
					If DrawButton(x+20*MenuScale,y+10*MenuScale,width/5,height/2, "GRAPCHICS", False) Then MainMenuTab = 3
					If DrawButton(x+160*MenuScale,y+10*MenuScale,width/5,height/2, "AULDIO", False) Then MainMenuTab = 5
					If DrawButton(x+300*MenuScale,y+10*MenuScale,width/5,height/2, "CONTRAILS", False) Then MainMenuTab = 6
					If DrawButton(x+440*MenuScale,y+10*MenuScale,width/5,height/2, "ASSVANCED", False) Then MainMenuTab = 7
					If DrawButton(x+20*MenuScale,y+35*MenuScale,width/5,height/2, "DONT TURN OFF", False) Then MainMenuTab = 9
				EndIf
				
				AASetFont Font1
				y = y + 70 * MenuScale
				
				If MainMenuTab <> 5
					UserTrackCheck% = 0
					UserTrackCheck2% = 0
				EndIf
				
				Local tx# = x+width
				Local ty# = y
				Local tw# = 400*MenuScale
				Local th# = 150*MenuScale
				
				;DrawOptionsTooltip(tx,ty,tw,th,"")
				
				If MainMenuTab = 3 ;Graphics
					;[Block]
					;height = 380 * MenuScale
					height = 360 * MenuScale
					DrawFrame(x, y, width, height)
					
					y=y+20*MenuScale
					
					Color 255,255,255				
					AAText(x + 20 * MenuScale, y, "Enable bump mapping:")	
					BumpEnabled = DrawTick(x + 310 * MenuScale, y + MenuScale, BumpEnabled)
					If MouseOn(x + 310 * MenuScale, y + MenuScale, 20*MenuScale,20*MenuScale) And OnSliderID=0
						;DrawTooltip("Not available in this version")
						DrawOptionsTooltip(tx,ty,tw,th,"bump")
					EndIf
					
					y=y+30*MenuScale
					
					Color 255,255,255
					AAText(x + 20 * MenuScale, y, "VSync:")
					Vsync% = DrawTick(x + 310 * MenuScale, y + MenuScale, Vsync%)
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"vsync")
					EndIf
					
					y=y+30*MenuScale
					
					Color 255,255,255
					AAText(x + 20 * MenuScale, y, "Anti-aliasing:")
					Opt_AntiAlias = DrawTick(x + 310 * MenuScale, y + MenuScale, Opt_AntiAlias%)
					;AAText(x + 20 * MenuScale, y + 15 * MenuScale, "(fullscreen mode only)")
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"antialias")
					EndIf
					
					y=y+30*MenuScale ;40
					
					Color 255,255,255
					AAText(x + 20 * MenuScale, y, "Enable room lights:")
					EnableRoomLights = DrawTick(x + 310 * MenuScale, y + MenuScale, EnableRoomLights)
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"roomlights")
					EndIf
					
					y=y+30*MenuScale
					
					;Local prevGamma# = ScreenGamma
					;If In3dMenu=0
						ScreenGamma = (SlideBar(x + 310*MenuScale, y+6*MenuScale, 150*MenuScale, ScreenGamma*50.0)/50.0)
					;EndIf
					Color 255,255,255
					AAText(x + 20 * MenuScale, y, "Screen gamma")
					If MouseOn(x+310*MenuScale,y+6*MenuScale,150*MenuScale+14,20) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"gamma",ScreenGamma)
					EndIf
					
					y=y+50*MenuScale
					
					Color 255,255,255
					AAText(x + 20 * MenuScale, y, "Particle amount:")
					ParticleAmount = Slider3(x+310*MenuScale,y+6*MenuScale,150*MenuScale,ParticleAmount,2,"MINIMAL","REDUCED","FULL")
					If (MouseOn(x + 310 * MenuScale, y-6*MenuScale, 150*MenuScale+14, 20) And OnSliderID=0) Or OnSliderID=2
						DrawOptionsTooltip(tx,ty,tw,th,"particleamount",ParticleAmount)
					EndIf
					
					y=y+50*MenuScale
					
					Color 255,255,255
					AAText(x + 20 * MenuScale, y, "Texture LOD Bias:")
					TextureDetails = Slider5(x+310*MenuScale,y+6*MenuScale,150*MenuScale,TextureDetails,3,"0.8","0.4","0.0","-0.4","-0.8")
					Select TextureDetails%
						Case 0
							TextureFloat# = 0.8
						Case 1
							TextureFloat# = 0.4
						Case 2
							TextureFloat# = 0.0
						Case 3
							TextureFloat# = -0.4
						Case 4
							TextureFloat# = -0.8
					End Select
					TextureLodBias TextureFloat
					If (MouseOn(x+310*MenuScale,y-6*MenuScale,150*MenuScale+14,20) And OnSliderID=0) Or OnSliderID=3
						DrawOptionsTooltip(tx,ty,tw,th+100*MenuScale,"texquality")
					EndIf
					
					y=y+50*MenuScale
					
					Color 255,255,255
					AAText(x + 20 * MenuScale, y, "Save textures in the VRAM:")
					EnableVRam = DrawTick(x + 310 * MenuScale, y + MenuScale, EnableVRam)
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"vram")
					EndIf
					
					y=y+30*MenuScale
					
					Color 255,255,255
					AAText(x + 20 * MenuScale, y, "Use Computer's Cursor Icon:")
					NoCursor = DrawTick(x + 310 * MenuScale, y + MenuScale, NoCursor)
					If MouseOn(x+310*MenuScale,y-6+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"nocursor")
					EndIf
					
					;y=y+30*MenuScale
					
					;Color 255,255,255
					;AAText(x + 20 * MenuScale, y, "???")
					;furri = DrawTick(x + 310 * MenuScale, y + MenuScale, furri)
					;If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
					;	DrawOptionsTooltip(tx,ty,tw,th,"furri")
					;EndIf
					
					;[End Block]
				ElseIf MainMenuTab = 5 ;Audio
					;[Block]
					If SubtitlesEnabled Then
						height = 280 * MenuScale
					Else
						height = 250 * MenuScale
					EndIf
					DrawFrame(x, y, width, height)
					
					y = y + 20*MenuScale
					
					MusicVolume = (SlideBar(x + 310*MenuScale, y-4*MenuScale, 150*MenuScale, MusicVolume*100.0)/100.0)
					Color 255,255,255
					AAText(x + 20 * MenuScale, y, "Music volume:")
					If MouseOn(x+310*MenuScale,y-4*MenuScale,150*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"musicvol",MusicVolume)
					EndIf
					
					y = y + 40*MenuScale
					
					;SFXVolume = (SlideBar(x + 310*MenuScale, y-4*MenuScale, 150*MenuScale, SFXVolume*100.0)/100.0)
					PrevSFXVolume = (SlideBar(x + 310*MenuScale, y-4*MenuScale, 150*MenuScale, SFXVolume*100.0)/100.0)
					SFXVolume = PrevSFXVolume
					Color 255,255,255
					AAText(x + 20 * MenuScale, y, "Sound volume:")
					If MouseOn(x+310*MenuScale,y-4*MenuScale,150*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"soundvol",PrevSFXVolume)
					EndIf
					;If MouseDown1 Then
					;	If MouseX() >= x And MouseX() <= x + width + 14 And MouseY() >= y And MouseY() <= y + 20 Then
					;		PlayTestSound(True)
					;	Else
					;		PlayTestSound(False)
					;	EndIf
					;Else
					;	PlayTestSound(False)
					;EndIf
					
					y = y + 30*MenuScale
					
					Color 255,255,255
					AAText x + 20 * MenuScale, y, "Sound auto-release:"
					EnableSFXRelease = DrawTick(x + 310 * MenuScale, y + MenuScale, EnableSFXRelease)
					If EnableSFXRelease_Prev% <> EnableSFXRelease
						If EnableSFXRelease%
							For snd.Sound = Each Sound
								For i=0 To 31
									If snd\channels[i]<>0 Then
										If ChannelPlaying(snd\channels[i]) Then
											StopChannel(snd\channels[i])
										EndIf
									EndIf
								Next
								If snd\internalHandle<>0 Then
									FreeSound snd\internalHandle
									snd\internalHandle = 0
								EndIf
								snd\releaseTime = 0
							Next
						Else
							For snd.Sound = Each Sound
								If snd\internalHandle = 0 Then snd\internalHandle = LoadSound(snd\name)
							Next
						EndIf
						EnableSFXRelease_Prev% = EnableSFXRelease
					EndIf
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th+220*MenuScale,"sfxautorelease")
					EndIf
					
					y = y + 30*MenuScale
					
					Color 255,255,255
					AAText x + 20 * MenuScale, y, "Enable Subtitles:"
					SubtitlesEnabled = DrawTick(x + 310 * MenuScale, y + MenuScale, SubtitlesEnabled)
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"subtitles")
					EndIf
					
					If SubtitlesEnabled Then
						y = y + 30*MenuScale
						
						Color 255,255,255
						AAText x + 20 * MenuScale, y, " -   Subtitles Language:"
						SelectedLanguage = DrawLangDropdown(x+310*MenuScale,y-4*MenuScale,SelectedLanguage,1,"Data\lang\")
						If MouseOn(x+310*MenuScale,y+MenuScale,137*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"subtitlelang")
						EndIf
					EndIf
					
					y = y + 30*MenuScale
					
					Color 255,255,255
					AAText x + 20 * MenuScale, y, "Enable user tracks:"
					EnableUserTracks = DrawTick(x + 310 * MenuScale, y + MenuScale, EnableUserTracks)
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"usertrack")
					EndIf
					
					If EnableUserTracks
						y = y + 30 * MenuScale
						Color 255,255,255
						AAText x + 20 * MenuScale, y, "User track mode:"
						UserTrackMode = DrawTick(x + 310 * MenuScale, y + MenuScale, UserTrackMode)
						If UserTrackMode
							AAText x + 350 * MenuScale, y + MenuScale, "Repeat"
						Else
							AAText x + 350 * MenuScale, y + MenuScale, "Random"
						EndIf
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"usertrackmode")
						EndIf
						If DrawButton(x + 20 * MenuScale, y + 30 * MenuScale, 190 * MenuScale, 25 * MenuScale, "Scan for User Tracks",False)
							DebugLog "User Tracks Check Started"
							
							UserTrackCheck% = 0
							UserTrackCheck2% = 0
							
							Dir=ReadDir("SFX\Radio\UserTracks\")
							Repeat
								file$=NextFile(Dir)
								If file$="" Then Exit
								If FileType("SFX\Radio\UserTracks\"+file$) = 1 Then
									UserTrackCheck = UserTrackCheck + 1
									test = LoadSound("SFX\Radio\UserTracks\"+file$)
									If test<>0
										UserTrackCheck2 = UserTrackCheck2 + 1
									EndIf
									FreeSound test
								EndIf
							Forever
							CloseDir Dir
							
							DebugLog "User Tracks Check Ended"
						EndIf
						If MouseOn(x+20*MenuScale,y+30*MenuScale,190*MenuScale,25*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"usertrackscan")
						EndIf
						If UserTrackCheck%>0
							AAText x + 20 * MenuScale, y + 100 * MenuScale, "User tracks found ("+UserTrackCheck2+"/"+UserTrackCheck+" successfully loaded)"
						EndIf
					Else
						UserTrackCheck%=0
					EndIf
					;[End Block]
				ElseIf MainMenuTab = 6 ;Controls
					;[Block]
					height = 270 * MenuScale
					DrawFrame(x, y, width, height)	
					
					y = y + 20*MenuScale
					
					MouseSens = (SlideBar(x + 310*MenuScale, y-4*MenuScale, 150*MenuScale, (MouseSens+0.5)*100.0)/100.0)-0.5
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Mouse sensitivity:")
					If MouseOn(x+310*MenuScale,y-4*MenuScale,150*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"mousesensitivity",MouseSens)
					EndIf
					
					y = y + 40*MenuScale
					
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Invert mouse Y-axis:")
					InvertMouse = DrawTick(x + 310 * MenuScale, y + MenuScale, InvertMouse)
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"mouseinvert")
					EndIf
					
					y = y + 40*MenuScale
					
					MouseSmooth = (SlideBar(x + 310*MenuScale, y-4*MenuScale, 150*MenuScale, (MouseSmooth)*50.0)/50.0)
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Mouse smoothing:")
					If MouseOn(x+310*MenuScale,y-4*MenuScale,150*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"mousesmoothing",MouseSmooth)
					EndIf
					
					Color(255, 255, 255)
					
					y = y + 30*MenuScale
					AAText(x + 20 * MenuScale, y, "Control configuration:")
					y = y + 10*MenuScale
					
					AAText(x + 20 * MenuScale, y + 20 * MenuScale, "Move Forward")
					InputBox(x + 160 * MenuScale, y + 20 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_UP,210)),5)		
					AAText(x + 20 * MenuScale, y + 40 * MenuScale, "Strafe Left")
					InputBox(x + 160 * MenuScale, y + 40 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_LEFT,210)),3)	
					AAText(x + 20 * MenuScale, y + 60 * MenuScale, "Move Backward")
					InputBox(x + 160 * MenuScale, y + 60 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_DOWN,210)),6)				
					AAText(x + 20 * MenuScale, y + 80 * MenuScale, "Strafe Right")
					InputBox(x + 160 * MenuScale, y + 80 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_RIGHT,210)),4)	
					AAText(x + 20 * MenuScale, y + 100 * MenuScale, "Quick Save")
					InputBox(x + 160 * MenuScale, y + 100 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_SAVE,210)),11)
					
					AAText(x + 280 * MenuScale, y + 20 * MenuScale, "Manual Blink")
					InputBox(x + 470 * MenuScale, y + 20 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_BLINK,210)),7)				
					AAText(x + 280 * MenuScale, y + 40 * MenuScale, "Sprint")
					InputBox(x + 470 * MenuScale, y + 40 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_SPRINT,210)),8)
					AAText(x + 280 * MenuScale, y + 60 * MenuScale, "Open/Close Inventory")
					InputBox(x + 470 * MenuScale, y + 60 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_INV,210)),9)
					AAText(x + 280 * MenuScale, y + 80 * MenuScale, "Crouch")
					InputBox(x + 470 * MenuScale, y + 80 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_CROUCH,210)),10)	
					AAText(x + 280 * MenuScale, y + 100 * MenuScale, "Open/Close Console")
					InputBox(x + 470 * MenuScale, y + 100 * MenuScale,100*MenuScale,20*MenuScale,KeyName(Min(KEY_CONSOLE,210)),12)
					
					If MouseOn(x+20*MenuScale,y,width-40*MenuScale,120*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"controls")
					EndIf
					
					For i = 0 To 227
						If KeyHit(i) Then key = i : Exit
					Next
					If key<>0 Then
						Select SelectedInputBox
							Case 3
								KEY_LEFT = key
							Case 4
								KEY_RIGHT = key
							Case 5
								KEY_UP = key
							Case 6
								KEY_DOWN = key
							Case 7
								KEY_BLINK = key
							Case 8
								KEY_SPRINT = key
							Case 9
								KEY_INV = key
							Case 10
								KEY_CROUCH = key
							Case 11
								KEY_SAVE = key
							Case 12
								KEY_CONSOLE = key
						End Select
						SelectedInputBox = 0
					EndIf
					;[End Block]
				ElseIf MainMenuTab = 7 ;Advanced
					;[Block]
					height = 380 * MenuScale
					DrawFrame(x, y, width, height)	
					
					y = y + 20*MenuScale
					
					Color 255,255,255				
					AAText(x + 20 * MenuScale, y, "Show HUD:")	
					HUDenabled = DrawTick(x + 310 * MenuScale, y + MenuScale, HUDenabled)
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"hud")
					EndIf
					
					y=y+30*MenuScale
					
					Color 255,255,255
					AAText(x + 20 * MenuScale, y, "Enable console:")
					CanOpenConsole = DrawTick(x + 310 * MenuScale, y + MenuScale, CanOpenConsole)
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"consoleenable")
					EndIf
					
					y = y + 30*MenuScale
					
					Color 255,255,255
					AAText(x + 20 * MenuScale, y, "Open console on error:")
					ConsoleOpening = DrawTick(x + 310 * MenuScale, y + MenuScale, ConsoleOpening)
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"consoleerror")
					EndIf
					
					y = y + 50*MenuScale
					
					Color 255,255,255
					AAText(x + 20 * MenuScale, y, "Achievement popups:")
					AchvMSGenabled% = DrawTick(x + 310 * MenuScale, y + MenuScale, AchvMSGenabled%)
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"achpopup")
					EndIf
					
					y = y + 50*MenuScale
					
					Color 255,255,255
					AAText(x + 20 * MenuScale, y, "Show FPS:")
					ShowFPS% = DrawTick(x + 310 * MenuScale, y + MenuScale, ShowFPS%)
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"showfps")
					EndIf
					
					y = y + 30*MenuScale
					
					Color 255,255,255
					AAText(x + 20 * MenuScale, y, "Framelimit:")
					Color 255,255,255
					If DrawTick(x + 310 * MenuScale, y, CurrFrameLimit > 0.0) Then
						;CurrFrameLimit# = (SlideBar(x + 150*MenuScale, y+30*MenuScale, 100*MenuScale, CurrFrameLimit#*50.0)/50.0)
						;CurrFrameLimit = Max(CurrFrameLimit, 0.1)
						;Framelimit% = CurrFrameLimit#*100.0
						CurrFrameLimit# = (SlideBar(x + 150*MenuScale, y+30*MenuScale, 100*MenuScale, CurrFrameLimit#*99.0)/99.0)
						CurrFrameLimit# = Max(CurrFrameLimit, 0.01)
						Framelimit% = 19+(CurrFrameLimit*100.0)
						Color 255,255,0
						AAText(x + 25 * MenuScale, y + 25 * MenuScale, Framelimit%+" FPS")
					Else
						CurrFrameLimit# = 0.0
						Framelimit = 0
					EndIf
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"framelimit",Framelimit)
					EndIf
					If MouseOn(x+150*MenuScale,y+30*MenuScale,100*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"framelimit",Framelimit)
					EndIf
					
					y = y + 50*MenuScale
					
					If IS_3DMENU_ENABLED Then
						Color 255,0,0
						LOCKEDAA% = True
					Else
						Color 255,255,255
						LOCKEDAA% = False
					EndIf
					AAText(x + 20 * MenuScale, y, "Antialiased text:")
					AATextEnable% = DrawTick(x + 310 * MenuScale, y + MenuScale, AATextEnable%, LOCKEDAA%)
					If AATextEnable_Prev% <> AATextEnable
						For font.AAFont = Each AAFont
							FreeFont font\lowResFont%
							If (Not AATextEnable)
								FreeTexture font\texture
								FreeImage font\backup
							EndIf
							Delete font
						Next
						If (Not AATextEnable) Or IS_3DMENU_ENABLED Then
							FreeEntity AATextCam
							;For i%=0 To 149
							;	FreeEntity AATextSprite[i]
							;Next
						EndIf
						If (Not IS_3DMENU_ENABLED)
							InitAAFont()
						EndIf
						If Not MemeMode
							Font1% = AALoadFont("GFX\font\cour\Courier New.ttf", Int(19 * (GraphicHeight / 1024.0)), 0,0,0)
							Font2% = AALoadFont("GFX\font\courbd\Courier New Bold.ttf", Int(58 * (GraphicHeight / 1024.0)), 1,0,0)
							Font3% = AALoadFont("GFX\font\DS-DIGI\DS-Digital.ttf", Int(22 * (GraphicHeight / 1024.0)), 0,0,0)
							Font4% = AALoadFont("GFX\font\DS-DIGI\DS-Digital.ttf", Int(60 * (GraphicHeight / 1024.0)), 0,0,0)
							Font5% = AALoadFont("GFX\font\Journal\Journal.ttf", Int(58 * (GraphicHeight / 1024.0)), 0,0,0)
							Font6% = AALoadFont("GFX\font\Futura\FuturaBlackBT-Regular.ttf", Int(60 * (GraphicHeight / 1024.0)), 0,0,0)
							FontChangelog% = AALoadFont("GFX\font\DS-DIGI\DS-Digital.ttf", Int(19 * (GraphicHeight / 1024.0)), 0,0,0)
						Else
							Font1% = AALoadFont("GFX\font\cour\Courier New.ttf", Int(19 * (GraphicHeight / 1024.0)), 0,0,0)
							Font2% = AALoadFont("GFX\font\sanic\z o n e.ttf", Int(80 * (GraphicHeight / 1024.0)), 0,0,0)
							Font3% = AALoadFont("GFX\font\DS-DIGI\DS-Digital.ttf", Int(22 * (GraphicHeight / 1024.0)), 0,0,0)
							Font4% = AALoadFont("GFX\font\DS-DIGI\DS-Digital.ttf", Int(60 * (GraphicHeight / 1024.0)), 0,0,0)
							Font5% = AALoadFont("GFX\font\Journal\Journal.ttf", Int(58 * (GraphicHeight / 1024.0)), 0,0,0)
							Font6% = AALoadFont("GFX\font\sanic\z o n e.ttf", Int(80 * (GraphicHeight / 1024.0)), 0,0,0)
							FontChangelog% = AALoadFont("GFX\font\DS-DIGI\DS-Digital.ttf", Int(19 * (GraphicHeight / 1024.0)), 0,0,0)
						EndIf
						ConsoleFont% = AALoadFont("Tahoma", Int(22 * (RealGraphicHeight / 1024.0)), 0,0,0,1)
						;ReloadAAFont()
						AATextEnable_Prev% = AATextEnable
					EndIf
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"antialiastext")
					EndIf
					
					y = y + 30*MenuScale
					If AATextEnable Then
						Color 255,0,0
						LOCKED3D% = True
					Else
						Color 255,255,255
						LOCKED3D% = False
					EndIf
					AAText(x + 20, y, "Enable 3D Menu:")
					IS_3DMENU_ENABLED% = DrawTick(x + 310 * MenuScale, y + MenuScale, IS_3DMENU_ENABLED%, LOCKED3D%)
					If IS_3DMENU_ENABLED_PREV% <> IS_3DMENU_ENABLED
						If (Not IS_3DMENU_ENABLED) Then
							DeInit3DMenu()
						Else
							Init3DMenu()
							Init3dMenuQuick()
						EndIf
						IS_3DMENU_ENABLED_PREV = IS_3DMENU_ENABLED
					EndIf
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"menu3d")
					EndIf
					
					y = y + 50*MenuScale
					Color 255,255,255
					AAText(x + 20, y, "Use launcher:")
					LauncherEnabled% = DrawTick(x + 310 * MenuScale, y + MenuScale, LauncherEnabled%)
					If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"uselauncher")
					EndIf
					
					;[End Block]
				ElseIf MainMenuTab = 9 ;funni meme mode
					;[Block]
					height = 360 * MenuScale
					DrawFrame(x, y, width, height)
					
					y=y+20*MenuScale
					
					Color 255,255,255				
					AAText(x + 20 * MenuScale, y, "Meme Mode (will restart game):")	
					;BumpEnabled = DrawTick(x + 310 * MenuScale, y + MenuScale, BumpEnabled)
					If MemeMode Then
						If DrawButton(x+310*MenuScale,y-10*MenuScale,width/5,height/10, "Disable", False) Then
							MemeMode = 0
							SaveOptionsINI()
							ExecFile Chr(34)+"SCP - Danger Breach Official.exe"+Chr(34)+" -nolauncher"
							End
						EndIf
					Else
						If DrawButton(x+310*MenuScale,y-10*MenuScale,width/5,height/10, "Enable", False) Then
							MemeMode = 1
							SaveOptionsINI()
							ExecFile Chr(34)+"SCP - Danger Breach Official.exe"+Chr(34)+" -nolauncher"
							End
						EndIf
					EndIf
					
					y=y+30*MenuScale
					
					Color 255,255,255				
					AAText(x + 20 * MenuScale, y, "Always override Intro Videos:")	
					MemeMode_Intros = DrawTick(x + 310 * MenuScale, y + MenuScale, MemeMode_Intros)
					;If MouseOn(x + 310 * MenuScale, y + MenuScale, 20*MenuScale,20*MenuScale) And OnSliderID=0
					;	;DrawTooltip("Not available in this version")
					;	DrawOptionsTooltip(tx,ty,tw,th,"mememode_introvids")
					;EndIf
					
					y=y+30*MenuScale
					
					Color 255,255,255				
					AAText(x + 20 * MenuScale, y, "Always override Loading Screens:")	
					MemeMode_Loading = DrawTick(x + 310 * MenuScale, y + MenuScale, MemeMode_Loading)
					;If MouseOn(x + 310 * MenuScale, y + MenuScale, 20*MenuScale,20*MenuScale) And OnSliderID=0
					;	;DrawTooltip("Not available in this version")
					;	DrawOptionsTooltip(tx,ty,tw,th,"mememode_loading")
					;EndIf
					;[End Block]
				EndIf
				;[End Block]
			Case 4 ; load map
				;[Block]
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				height = 510 * MenuScale
				
				DrawFrame(x, y, width, height)
				
				x = 159 * MenuScale
				y = 286 * MenuScale
				
				width = 400 * MenuScale
				height = 70 * MenuScale
				
				Color(255, 255, 255)
				AASetFont Font2
				If (Not MemeMode) Then AAText(x + width / 2, y + height / 2, "LOAD MAP", True, True)
				If (MemeMode) Then AAText(x + width / 2, y + height / 2, "GOOFY AHH", True, True)
				
				x = 160 * MenuScale
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				height = 350 * MenuScale
				
				AASetFont Font2
				
				tx# = x+width
				ty# = y
				tw# = 400*MenuScale
				th# = 150*MenuScale
				
				If CurrLoadGamePage < Ceil(Float(SavedMapsAmount)/6.0)-1 Then 
					If DrawButton(x+530*MenuScale, y + 510*MenuScale, 50*MenuScale, 55*MenuScale, ">") Then
						CurrLoadGamePage = CurrLoadGamePage+1
					EndIf
				Else
					DrawFrame(x+530*MenuScale, y + 510*MenuScale, 50*MenuScale, 55*MenuScale)
					Color(100, 100, 100)
					AAText(x+555*MenuScale, y + 537.5*MenuScale, ">", True, True)
				EndIf
				If CurrLoadGamePage > 0 Then
					If DrawButton(x, y + 510*MenuScale, 50*MenuScale, 55*MenuScale, "<") Then
						CurrLoadGamePage = CurrLoadGamePage-1
					EndIf
				Else
					DrawFrame(x, y + 510*MenuScale, 50*MenuScale, 55*MenuScale)
					Color(100, 100, 100)
					AAText(x+25*MenuScale, y + 537.5*MenuScale, "<", True, True)
				EndIf
				
				DrawFrame(x+50*MenuScale,y+510*MenuScale,width-100*MenuScale,55*MenuScale)
				
				AAText(x+(width/2.0),y+536*MenuScale,"Page "+Int(Max((CurrLoadGamePage+1),1))+"/"+Int(Max((Int(Ceil(Float(SavedMapsAmount)/6.0))),1)),True,True)
				
				AASetFont Font1
				
				If CurrLoadGamePage > Ceil(Float(SavedMapsAmount)/6.0)-1 Then
					CurrLoadGamePage = CurrLoadGamePage - 1
				EndIf
				
				AASetFont Font1
				
				If SavedMaps(0)="" Then 
					AAText (x + 20 * MenuScale, y + 20 * MenuScale, "No saved maps. Use the Map Creator to create new maps.")
				Else
					x = x + 20 * MenuScale
					y = y + 20 * MenuScale
					For i = (1+(6*CurrLoadGamePage)) To 6+(6*CurrLoadGamePage)
						If i <= SavedMapsAmount Then
							DrawFrame(x,y,540* MenuScale, 70* MenuScale)
							
							AAText(x + 20 * MenuScale, y + 10 * MenuScale, SavedMaps(i - 1))
							AAText(x + 20 * MenuScale, y + (10+27) * MenuScale, SavedMapsAuthor(i - 1))
							
							If DrawButton(x + 400 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, "Load", False) Then
								SelectedMap=SavedMaps(i - 1)
								MainMenuTab = 1
							EndIf
							If MouseOn(x + 400 * MenuScale, y + 20 * MenuScale, 100*MenuScale,30*MenuScale)
								DrawMapCreatorTooltip(tx,ty,tw,th,SavedMaps(i-1))
							EndIf
							
							y = y + 80 * MenuScale
						Else
							Exit
						EndIf
					Next
				EndIf
				;[End Block]
			Case 8 ; CHANGELOG
			;[Block]
				
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				;height = 300 * MenuScale
				height = 510 * MenuScale
				
				DrawFrame(x, y, width, height)
				
				x = 159 * MenuScale
				y = 286 * MenuScale
				
				width = 400 * MenuScale
				height = 70 * MenuScale
				
				Color(255, 255, 255)
				AASetFont Font2
				If (Not MemeMode) Then AAText(x + width / 2, y + height / 2, "CHANGELOG", True, True)
				If (MemeMode) Then AAText(x + width / 2, y + height / 2, "CHANGELOG", True, True)
				
				x = 160 * MenuScale
				y = y + height + 20 * MenuScale
				width = 680 * MenuScale
				height = 296 * MenuScale
				
				AASetFont FontChangelog	
				
				If (Not MemeMode)
					If FileSize("Changelog.txt")=0 Then
						AAText (x + 20 * MenuScale, y + 20 * MenuScale, "Unable to load ChangeLog - 'changelog.txt' was not found")
						If changelogLoaded = False Then DebugLog "changelog missing"
					Else
						
						Local filein
						
						Local ReadTotal$ = ""
						
						filein = ReadFile("Changelog.txt") 
						
						Local Check$ = ""
						
						Local StartingY% = 20
						
						While Not(Check$="------------------------------------------------------------")
							ReadTotal$ = ReadLine$( filein )
							Check$ = ReadTotal$
							AAText (x + 20 * MenuScale, y + StartingY * MenuScale, ReadTotal$)
							
							StartingY = StartingY + 15
								
						Wend
							
						CloseFile filein
						
						If changelogLoaded = False Then DebugLog "Loaded Changelog!"
						
					EndIf
				Else
					AAText (x + 20 * MenuScale, y + 20 * MenuScale, "fuck you")
				End If
					
				changelogLoaded = True
				
				;Print "Integer Data Read From File - myFile.txt " 
				;Print Read1 
				
				;Read1$ = GetINIString(filein, TemporaryString, "Danger Breach v")
				
				;[End Block]
				CatchErrors("UpdateMainMenu")
		End Select
		
	End If
	
	DrawAllMenuDropdowns()
	
	Color 255,255,255
	AASetFont ConsoleFont
	If Not MemeMode
		versionTextTemp = "SCP - Danger Breach v"+VersionNumber
		AAText 20,RealGraphicHeight-90,versionTextTemp
		versionTextTemp = "Developed by Action Games"
		AAText 20,RealGraphicHeight-70,versionTextTemp
		versionTextTemp = "Built upon SCP - Containment Breach v1.3.11"
		AAText 20,RealGraphicHeight-50,versionTextTemp
		versionTextTemp = "Made using Blitz3D SoLoud: MAV-Less v"+EngineVersionNumber
	Else
		versionTextTemp = "Sponsered by RedBull"
	EndIf
	AAText 20,RealGraphicHeight-30,versionTextTemp
	
	;DrawTiledImageRect(MenuBack, 985 * MenuScale, 860 * MenuScale, 200 * MenuScale, 20 * MenuScale, 1200 * MenuScale, 866 * MenuScale, 300, 20 * MenuScale)
	
	If NoCursor = False Then
		HidePointer 
		DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
	EndIf
	
	AASetFont Font1
End Function

Type MenuDropdown
	Field x%,y%
	Field value%
	Field ID%
	Field txt1$,txt2$,txt3$,txt4$,txt5$,txt6$,txt7$,txt8$
	Field size%
End Type	

Function DrawAllMenuDropdowns()
	Local md.MenuDropdown
	Local txt$
	Local i%
	For md = Each MenuDropdown
		DrawFrame(md\x, md\y, 120 * MenuScale, 30 * MenuScale)
		Select md\value
			Case 0
				txt = md\txt1
			Case 1
				txt = md\txt2
			Case 2
				txt = md\txt3
			Case 3
				txt = md\txt4
			Case 4
				txt = md\txt5
			Case 5
				txt = md\txt6
			Case 6
				txt = md\txt7
			Case 7
				txt = md\txt8
		End Select
		Text(md\x + (120/2) * MenuScale, md\y + 15 * MenuScale, txt, True, True)
		Color(255, 255, 255)
	Next
	
	;Drawing the selection box when selected
	For md = Each MenuDropdown
		If SelectedInputBox = md\ID Then
			;Why 30.5? I have no idea!
			DrawFrame(md\x, md\y + (30.5 - FRAME_THICK) * MenuScale, (120 + 30 - FRAME_THICK) * MenuScale, (20 + (20 * md\size)) * MenuScale)
			For i = 0 To md\size-1
				Color(255, 255, 255)
				Select i
					Case 0
						txt = md\txt1
					Case 1
						txt = md\txt2
					Case 2
						txt = md\txt3
					Case 3
						txt = md\txt4
					Case 4
						txt = md\txt5
					Case 5
						txt = md\txt6
					Case 6
						txt = md\txt7
					Case 7
						txt = md\txt8
				End Select
				Text(md\x + ((120 + 30 - FRAME_THICK)/2) * MenuScale, md\y + (30.5 - FRAME_THICK + 20 + 20 * i) * MenuScale, txt, True, True)
				If MouseOn(md\x + 5.5 * MenuScale, md\y + (20 - FRAME_THICK + 20 + 20 * i) * MenuScale, 137 * MenuScale, 20 * MenuScale) Then
					Color(100, 100, 100)
					Rect(md\x + 5.5 * MenuScale, md\y + (20 - FRAME_THICK + 20 + 20 * i) * MenuScale, 137 * MenuScale, 20 * MenuScale, False)
					;DrawOptionsTooltip(txt, i)
				EndIf
			Next
			Exit
		EndIf
	Next
	
End Function	

Function DeleteMenuGadgets()
	
	Delete Each MenuDropdown
	
End Function

Function UpdateLauncher()
	MenuScale = 1
	
	Local WillExit=False
	
	Graphics3DExt(LauncherWidth, LauncherHeight, 0, 2)

	;InitExt
	
	SetBuffer BackBuffer()
	
	RealGraphicWidth = GraphicWidth
	RealGraphicHeight = GraphicHeight
	
	Font1 = LoadFont_Strict("GFX\font\vcr_osd_mono\VCR OSD Mono.ttf", 18, 0,0,0)
	SetFont Font1
	
	MenuWhite = LoadImage_Strict("GFX\menu\menuwhite.jpg")
	MenuBlack = LoadImage_Strict("GFX\menu\menublack.jpg")	
	MaskImage MenuBlack, 255,255,0
	LauncherIMG = LoadImage_Strict("GFX\menu\launcherShared.jpg")
	;ButtonSFX% = LoadSound_Strict("SFX\Interact\Button.ogg")
	Local i%
	
	For i = 0 To 3
		ArrowIMG(i) = LoadImage_Strict("GFX\menu\arrow.png")
		RotateImage(ArrowIMG(i), 90 * i)
		HandleImage(ArrowIMG(i), 0, 0)
	Next
	
	For i% = 1 To TotalGFXModes
		Local samefound% = False
		For n% = 0 To TotalGFXModes - 1
			If GfxModeWidths(n) = GfxModeWidth(i) And GfxModeHeights(n) = GfxModeHeight(i) Then samefound = True : Exit
		Next
		If samefound = False Then
			If GraphicWidth = GfxModeWidth(i) And GraphicHeight = GfxModeHeight(i) Then SelectedGFXMode = GFXModes
			GfxModeWidths(GFXModes) = GfxModeWidth(i)
			GfxModeHeights(GFXModes) = GfxModeHeight(i)
			GFXModes=GFXModes+1 
		End If
	Next
	
	BlinkMeterIMG% = LoadImage_Strict("GFX\blinkmeter.jpg")
	CheckForUpdates()
	
	AppTitle GameIdent+GameIdentStrSeperator+"v"+VersionNumber+" - Launcher"
	
	;BlitzcordSetActivityState("In Launcher")

	;BlitzcordSetActivityDetails("SCPDB v"+VersionNumber)

	;BlitzcordUpdateActivity()
	
	;ResizeImage(LauncherIMG,LauncherWidth,LauncherHeight)
	
	;Local resizedWidth% = ((LauncherWidth * 1000) / 640) * 0.001
	
	;Local resizedHeight% = ((LauncherHeight * 1000) / 480) * 0.001
	
	CountGfxDrivers()
	
	Repeat
		
		;Cls
		Color 0,0,0
		;Rect 0,0,LauncherWidth,LauncherHeight,True
		DrawTiledImageRect(MenuBlack, 0, 0, 512, 512, 0, 0, LauncherWidth, LauncherHeight)
		
		MouseHit1 = MouseHit(1)
		
		Color 255, 255, 255
		DrawImage(LauncherIMG, 0, 0)
		
		Text(20, 240 - 67.5, "Resolution: ")
		
		Local x% = 40
		Local y% = 270 - 65
		For i = 0 To (GFXModes - 1)
			Color 0, 0, 0
			If SelectedGFXMode = i Then Rect(x - 1, y - 1, 100, 20, False)
			
			Text(x, y, (GfxModeWidths(i) + "x" + GfxModeHeights(i)))
			If MouseOn(x - 1, y - 1, 100, 20) Then
				Color 100, 100, 100
				Rect(x - 1, y - 1, 100, 20, False)
				If MouseHit1 Then SelectedGFXMode = i
			EndIf
			
			y=y+20
			If y >= 250 - 65 + (LauncherHeight - 80 - 260) Then y = 270 - 65 : x=x+100
		Next
		
		;-----------------------------------------------------------------
		Color 255, 255, 255
		x = 30
		y = 489
		DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x - 10, y, 340, 95)
		;Rect(x - 10, y, 340, 95)
		Text(x - 10, y - 30, "Graphics:")
		
		y=y+10
		For i = 1 To CountGfxDrivers()
			Color 0, 0, 0
			Local txt$
			If GfxDriverName(SelectedGFXDriver) = ""
				SelectedGFXDriver = 1
			EndIf
			If SelectedGFXDriver = i Then Rect(x - 1, y - 1, 290, 20, False)
			;text(x, y, bbGfxDriverName(i))
			If i = 1
				LimitText("Primary Graphics Card", x, y, 290, False)
			Else
				LimitText(GfxDriverName(i), x, y, 290, False)
			EndIf
			If MouseOn(x - 1, y - 1, 290, 20) Then
				Color 100, 100, 100
				Rect(x - 1, y - 1, 290, 20, False)
				If MouseHit1 Then SelectedGFXDriver = i
			EndIf
			
			y=y+20
		Next
		
		Fullscreen = DrawTick(40 + 430 - 15, 260 - 55 + 5 - 8, Fullscreen, BorderlessWindowed)
		BorderlessWindowed = DrawTick(40 + 430 - 15, 260 - 55 + 35, BorderlessWindowed)
		lock% = False
		
		If BorderlessWindowed Or (Not Fullscreen) Then lock% = True
		Bit16Mode = DrawTick(40 + 430 - 15, 260 - 55 + 65 + 8, Bit16Mode,lock%)
		LauncherEnabled = DrawTick(40 + 430 - 15, 260 - 55 + 95 + 8, LauncherEnabled)
		
		If BorderlessWindowed
			Color 255, 0, 0
			Fullscreen = False
		Else
			Color 255, 255, 255
		EndIf
		
		Text(40 + 430 + 15, 262 - 55 + 5 - 8, "Fullscreen")
		Color 255, 255, 255
		Text(40 + 430 + 15, 262 - 55 + 35 - 8, "Borderless",False,False)
		Text(40 + 430 + 15, 262 - 55 + 35 + 12, "windowed mode",False,False)
		
		If BorderlessWindowed Or (Not Fullscreen)
			Color 255, 0, 0
			Bit16Mode = False
		Else
		    Color 255, 255, 255
		EndIf
		
		Text(40 + 430 + 15, 262 - 55 + 65 + 8, "16 Bit")
		Color 255, 255, 255
		Text(40 + 430 + 15, 262 - 55 + 95 + 8, "Use launcher")
		
		If (Not BorderlessWindowed)
			If Fullscreen
			Text(40+ 260 + 15, 489 - 30, "Current Resolution: "+(GfxModeWidths(SelectedGFXMode) + "x" + GfxModeHeights(SelectedGFXMode) + "," + (16+(16*(Not Bit16Mode)))))
			Else
			Text(40+ 260 + 15, 489 - 30, "Current Resolution: "+(GfxModeWidths(SelectedGFXMode) + "x" + GfxModeHeights(SelectedGFXMode) + ",32"))
			EndIf
		Else
		Text(40+ 260 + 15, 489 - 30, "Current Resolution: "+GfxModeWidths(SelectedGFXMode) + "x" + GfxModeHeights(SelectedGFXMode) + ",32")
			If GfxModeWidths(SelectedGFXMode)<G_viewport_width Then
			Text(40+ 260 + 65, 489 - 10, "(upscaled to")
			Text(40+ 260 + 77.5, 489 + 10, G_viewport_width + "x" + G_viewport_height + ",32)")
			ElseIf GfxModeWidths(SelectedGFXMode)>G_viewport_width Then
			Text(40+ 260 + 65, 489 - 10, "(downscaled to")
			Text(40+ 260 + 77.5, 489 + 10, G_viewport_width + "x" + G_viewport_height + ",32)")
			EndIf
		EndIf
		
		UpdateCheckEnabled = DrawTick(LauncherWidth - 275, LauncherHeight - 50, UpdateCheckEnabled)
		Color 255,255,255
		Text LauncherWidth-250,LauncherHeight-70,"Check for"
		Text LauncherWidth-250,LauncherHeight-50,"updates on"
		Text LauncherWidth-250,LauncherHeight-30,"launch"
		
		If DrawButton(LauncherWidth - 30 - 120, LauncherHeight - 50 - 220, 140, 30, "LAUNCH GAME", False, False, False) Then
		GraphicWidth = GfxModeWidths(SelectedGFXMode)
		GraphicHeight = GfxModeHeights(SelectedGFXMode)
		RealGraphicWidth = GraphicWidth
		RealGraphicHeight = GraphicHeight
		DebugLog "GFX Driver is "+SelectedGFXDriver
		SetGfxDriver(SelectedGFXDriver)
		Font1 = LoadFont_Strict("GFX\font\cour\Courier New.ttf", 18, 0,0,0)
		Exit
		EndIf
		
		If DrawButton(LauncherWidth - 30 - 120, LauncherHeight - 50 - 165, 140, 30, "MAP BUILDER", False, False, False) Then
			ChangeDir "Map Creator"
			If FileSize("MapBuilder.exe")=0 Then RuntimeError("Could not open '../Map Creator/MapBuilder.exe', file is invalid or simply does not exist.")
			ExecFile("MapBuilder.exe")
			WillExit=True
			Exit
		EndIf
		
		If DrawButton(LauncherWidth - 30 - 115, LauncherHeight - 50 - 110, 130, 30, "RMESH VIEW", False, False, False) Then
			If FileSize("RMesh_Model_Viewer.exe")=0 Then RuntimeError("Could not open '../RMesh_Model_Viewer.exe', file is invalid or simply does not exist.")
			ExecFile("RMesh_Model_Viewer.exe")
			WillExit=True
			Exit
		EndIf
		
		If DrawButton(LauncherWidth - 30 - 110, LauncherHeight - 50 - 55, 120, 30, "CHANGELOG", False, False, False) Then
			If FileSize("Changelog.txt")=0 Then RuntimeError("Could not open '../Changelog.txt', file is invalid or simply does not exist.")
			ExecFile("Changelog.txt")
		End If
		
		If DrawButton(LauncherWidth - 30 - 100, LauncherHeight - 50, 100, 30, "EXIT", False, False, False) Then
			WillExit=True
			Exit
		EndIf
		Flip
	Forever
	
	PutINIValue(OptionFile, "options", "width", GfxModeWidths(SelectedGFXMode))
	PutINIValue(OptionFile, "options", "height", GfxModeHeights(SelectedGFXMode))
	If Fullscreen Then
		PutINIValue(OptionFile, "options", "fullscreen", "true")
	Else
		PutINIValue(OptionFile, "options", "fullscreen", "false")
	EndIf
	If LauncherEnabled Then
		PutINIValue(OptionFile, "launcher", "launcher enabled", "true")
	Else
		PutINIValue(OptionFile, "launcher", "launcher enabled", "false")
	EndIf
	If BorderlessWindowed Then
		PutINIValue(OptionFile, "options", "borderless windowed", "true")
	Else
		PutINIValue(OptionFile, "options", "borderless windowed", "false")
	EndIf
	If Bit16Mode Then
		PutINIValue(OptionFile, "options", "16bit", "true")
	Else
		PutINIValue(OptionFile, "options", "16bit", "false")
	EndIf
	PutINIValue(OptionFile, "options", "gfx driver", SelectedGFXDriver)
	If UpdateCheckEnabled Then
		PutINIValue(OptionFile, "options", "check for updates", "true")
	Else
		PutINIValue(OptionFile, "options", "check for updates", "false")
	EndIf
	
	If WillExit=True Then End
	
	;If Fullscreen Then
	;HidePointer 
	;DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
	
End Function


Function DrawTiledImageRect(img%, srcX%, srcY%, srcwidth#, srcheight#, x%, y%, width%, height%)
	
	Local x2% = x
	While x2 < x+width
		If x2 + srcwidth > x + width Then srcwidth = (x + width) - x2
		Local y2% = y
		While y2 < y+height
			If x2 + srcwidth > x + width Then srcwidth = srcwidth - Max((x2 + srcwidth) - (x + width), 1)
			If y2 + srcheight > y + height Then srcheight = srcheight - Max((y2 + srcheight) - (y + height), 1)
			DrawImageRect(img, x2, y2, srcX, srcY, srcwidth, Min((y + height) - y2, srcheight))
			y2 = y2 + srcheight
		Wend
		x2 = x2 + srcwidth
	Wend
	
End Function



Type LoadingScreens
	Field imgpath$
	Field img%
	Field ID%
	Field title$
	Field alignx%, aligny%
	Field disablebackground%
	Field txt$[5], txtamount%
	Field isAnim%, animW%, animH%, animOffset%, animCount%, animFrame%, animFramerate%, animFrameTime%
End Type

Function InitLoadingScreens(file$)
	Local TemporaryString$, i%
	Local ls.LoadingScreens
	
	Local f = OpenFile(file)
	
	While Not Eof(f)
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString,1) = "[" Then
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			
			ls.LoadingScreens = New LoadingScreens
			LoadingScreenAmount=LoadingScreenAmount+1
			ls\ID = LoadingScreenAmount
			
			ls\title = TemporaryString
			ls\imgpath = GetINIString(file, TemporaryString, "image path")
			
			For i = 0 To 4
				ls\txt[i] = GetINIString(file, TemporaryString, "text"+(i+1))
				If ls\txt[i]<> "" Then ls\txtamount=ls\txtamount+1
			Next
			
			ls\disablebackground = GetINIInt(file, TemporaryString, "disablebackground")
			
			Select Lower(GetINIString(file, TemporaryString, "align x"))
				Case "left"
					ls\alignx = -1
				Case "middle", "center"
					ls\alignx = 0
				Case "right" 
					ls\alignx = 1
			End Select 
			
			Select Lower(GetINIString(file, TemporaryString, "align y"))
				Case "top", "up"
					ls\aligny = -1
				Case "middle", "center"
					ls\aligny = 0
				Case "bottom", "down"
					ls\aligny = 1
			End Select 				
			ls\isAnim = GetINIInt(file, TemporaryString, "anim")
			
			If ls\isAnim
				ls\animW = GetINIInt(file, TemporaryString, "animWidth")
				ls\animH = GetINIInt(file, TemporaryString, "animHeight")
				ls\animOffset = GetINIInt(file, TemporaryString, "animOffset")
				ls\animCount = GetINIInt(file, TemporaryString, "animFrameCount")
				ls\animFramerate = GetINIInt(file, TemporaryString, "animMillisecondDelay")
			EndIf
			
		EndIf
	Wend
	
	CloseFile f
End Function

Function DrawLoading(percent%, shortloading=False, hideCur=False, autoClose=False)
	CatchErrors("Uncaught (DrawLoading)")
	
	Local x%, y%
	
	LoadingFast=shortloading
	
	CurrentLoadingPercent = percent
	
	If percent = 0 Then
		LoadingScreenText=0
		
		temp = Rand(1,LoadingScreenAmount)
		For ls.LoadingScreens = Each LoadingScreens
			If ls\ID = temp Then
				If (Not ls\isAnim)
					If ls\img=0 Then ls\img = LoadImage_Strict("Loadingscreens\"+ls\imgpath)
					SelectedLoadingScreen = ls 
					Exit
				Else
					If ls\img=0 Then ls\img = LoadAnimImage_Strict("Loadingscreens\"+ls\imgpath,ls\animW,ls\animH,ls\animOffset,ls\animCount)
					SelectedLoadingScreen = ls 
					Exit
				EndIf
			EndIf
		Next
	EndIf	
	
	If hideCur=True Then
		If NoCursor=False Then
			HidePointer 
			DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
		EndIf
	Else
		ShowPointer
	EndIf
	
	firstloop = True
	Repeat 
		
		;Color 0,0,0
		;Rect 0,0,GraphicWidth,GraphicHeight,True
		;Color 255, 255, 255
		ClsColor 0,0,0
		Cls
		
		UpdateBlitzcord()
		
		;SyncGame()
		
		;Cls(True,False)
		
		If percent > 20 Then
			UpdateMusic()
		EndIf
		
		If shortloading = False Then
			If percent > (100.0 / SelectedLoadingScreen\txtamount)*(LoadingScreenText+1) Then
				LoadingScreenText=LoadingScreenText+1
			EndIf
		EndIf
		
		If (Not SelectedLoadingScreen\disablebackground) Then
			DrawImage LoadingBack, GraphicWidth/2 - ImageWidth(LoadingBack)/2, GraphicHeight/2 - ImageHeight(LoadingBack)/2
		EndIf	
		
		If SelectedLoadingScreen\alignx = 0 Then
			x = GraphicWidth/2 - ImageWidth(SelectedLoadingScreen\img)/2 
		ElseIf  SelectedLoadingScreen\alignx = 1
			x = GraphicWidth - ImageWidth(SelectedLoadingScreen\img)
		Else
			x = 0
		EndIf
		
		If SelectedLoadingScreen\aligny = 0 Then
			y = GraphicHeight/2 - ImageHeight(SelectedLoadingScreen\img)/2 
		ElseIf  SelectedLoadingScreen\aligny = 1
			y = GraphicHeight - ImageHeight(SelectedLoadingScreen\img)
		Else
			y = 0
		EndIf	
		
		If (Not SelectedLoadingScreen\isAnim)
			DrawImage SelectedLoadingScreen\img, x, y
		Else
			If MilliSecs() > SelectedLoadingScreen\animFrameTime + SelectedLoadingScreen\animFramerate Then
				SelectedLoadingScreen\animFrameTime=MilliSecs() ; 'reset' the timer
				SelectedLoadingScreen\animFrame=( SelectedLoadingScreen\animFrame + 1 ) Mod 3 ; increment the frame, flip to 0 if we are out
			End If
			DrawImage SelectedLoadingScreen\img, x, y, SelectedLoadingScreen\animFrame
		EndIf
		
		Local width% = 300, height% = 20
		x% = GraphicWidth / 2 - width / 2
		y% = GraphicHeight / 2 + 30 - 100
		
		Rect(x, y, width+4, height, False)
		For  i% = 1 To Int((width - 2) * (percent / 100.0) / 10)
			DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
		Next
		
		If SelectedLoadingScreen\title = "CWM" Then
			
			If Not shortloading Then 
				If firstloop Then 
					If percent = 0 Then
						PlaySound_Strict LoadTempSound("SFX\SCP\990\cwm1.cwm")
					ElseIf percent = 100
						PlaySound_Strict LoadTempSound("SFX\SCP\990\cwm2.cwm")
					EndIf
				EndIf
			EndIf
			
			AASetFont Font2
			strtemp$ = ""
			temp = Rand(2,9)
			For i = 0 To temp
				strtemp$ = STRTEMP + Chr(Rand(48,122))
			Next
			AAText(GraphicWidth / 2, GraphicHeight / 2 + 80, strtemp, True, True)
			
			If percent = 0 Then 
				LoadStarted = 0
				If Rand(5)=1 Then
					Select Rand(2)
						Case 1
							SelectedLoadingScreen\txt[0] = "It will happen on " + CurrentDate() + "."
						Case 2
							SelectedLoadingScreen\txt[0] = CurrentTime()
					End Select
				Else
					Select Rand(13)
						Case 1
							SelectedLoadingScreen\txt[0] = "A very fine radio might prove to be useful."
						Case 2
							SelectedLoadingScreen\txt[0] = "ThIS PLaCE WiLL BUrN"
						Case 3
							SelectedLoadingScreen\txt[0] = "You cannot control it."
						Case 4
							SelectedLoadingScreen\txt[0] = "eof9nsd3jue4iwe1fgj"
						Case 5
							SelectedLoadingScreen\txt[0] = "YOU NEED TO TRUST IT"
						Case 6 
							SelectedLoadingScreen\txt[0] = "Look my friend in the eye when you address him, isn't that the way of the gentleman?"
						Case 7
							SelectedLoadingScreen\txt[0] = "???____??_???__????n?"
						Case 8, 9
							SelectedLoadingScreen\txt[0] = "Jorge has been expecting you."
						Case 10
							SelectedLoadingScreen\txt[0] = "???????????"
						Case 11
							SelectedLoadingScreen\txt[0] = "Make her a member of the midnight crew."
						Case 12
							SelectedLoadingScreen\txt[0] = "oncluded that coming here was a mistake. We have to turn back."
						Case 13
							SelectedLoadingScreen\txt[0] = "This alloy contains the essence of my life."
					End Select
				EndIf
			EndIf
			
			strtemp$ = SelectedLoadingScreen\txt[0]
			temp = Int(Len(SelectedLoadingScreen\txt[0])-Rand(5))
			For i = 0 To Rand(10,15);temp
				strtemp$ = Replace(SelectedLoadingScreen\txt[0],Mid(SelectedLoadingScreen\txt[0],Rand(1,Len(strtemp)-1),1),Chr(Rand(130,250)))
			Next		
			AASetFont Font1
			RowText(strtemp, GraphicWidth / 2-200, GraphicHeight / 2 +120,400,300,True)		
		Else
			
			Color 0,0,0
			AASetFont Font2
			AAText(GraphicWidth / 2 + 1, GraphicHeight / 2 + 80 + 1, SelectedLoadingScreen\title, True, True)
			AASetFont Font1
			RowText(SelectedLoadingScreen\txt[LoadingScreenText], GraphicWidth / 2-200+1, GraphicHeight / 2 +120+1,400,300,True)
			
			Color 255,255,255
			AASetFont Font2
			AAText(GraphicWidth / 2, GraphicHeight / 2 +80, SelectedLoadingScreen\title, True, True)
			AASetFont Font1
			RowText(SelectedLoadingScreen\txt[LoadingScreenText], GraphicWidth / 2-200, GraphicHeight / 2 +120,400,300,True)
			
		EndIf
		
		Color 0,0,0
		AAText(GraphicWidth / 2 + 1, GraphicHeight / 2 - 100 + 1, "LOADING - " + percent + " %", True, True)
		Local LoadIcon%
		If Not percent = 100 Then
			;If LoadIcon = 0 Then LoadIcon = LoadTexture_Strict("GFX\menu\QuickLoading.png",3)
			If LoadIcon = 0 Then LoadIcon = LoadImage_Strict("GFX\menu\QuickLoading.png")
			ResizeImage(LoadIcon, ImageWidth(LoadIcon) * MenuScale, ImageHeight(LoadIcon) * MenuScale)
			MidHandle LoadIcon
			DrawImage LoadIcon,MenuScale*140,RealGraphicHeight / 1.08
		Else
			If LoadIcon <> 0 Then FreeImage(LoadIcon)
		EndIf
		Color 255,255,255
		AAText(GraphicWidth / 2, GraphicHeight / 2 - 100, "LOADING - " + percent + " %", True, True)
		
		If firstloop Then
			AppTitle GameIdent+GameIdentStrSeperator+"v"+VersionNumber+" - Loading..."
			BlitzcordGameStatus=3
		EndIf
		
		LoadStarted = 1
		
		If percent = 100 Then 
			If firstloop And SelectedLoadingScreen\title <> "CWM" And TitleHappened = 0 Then PlaySound_Strict LoadTempSound(("SFX\Horror\Horror8.ogg"))
			If firstloop And SelectedLoadingScreen\title <> "CWM" And TitleHappened = 1 Then PlaySound_Strict LoadTempSound(("SFX\Ambient\General\Ambient16.ogg"))
				If TitleHappened = 0 Then
				;	If Not Fullscreen Then
				;		AAText(GraphicWidth / 2, GraphicHeight - (30 * MenuScale), "TITLE SCREEN LOADED", True, True)
				;	Else
				;		AAText(GraphicWidth / 2, GraphicHeight - (30 * MenuScale), "TITLE SCREEN LOADED", True, True)
				;	EndIf
					AppTitle GameIdent+GameIdentStrSeperator+"v"+VersionNumber+" - Title Screen"
					BlitzcordGameStatus=1
					If autoClose
						AAText(GraphicWidth / 2, RealGraphicHeight - 30, "PLEASE WAIT, THE GAME WILL PROCEED TO THE TITLE-SCREEN AUTOMATICALLY", True, True)
					Else
						AAText(GraphicWidth / 2, RealGraphicHeight - 30, "PRESS ANY KEY TO CONTINUE TO THE TITLE-SCREEN", True, True)
					EndIf
				Else
				;	If Not Fullscreen Then
				;		AAText(GraphicWidth / 2, GraphicHeight - 30, "LEVEL LOADED", True, True)
				;	Else
				;		AAText(GraphicWidth / 2, GraphicHeight - 30, "LEVEL LOADED", True, True)
				;	EndIf
					AppTitle GameIdent+GameIdentStrSeperator+"v"+VersionNumber+" - Main Game"
					BlitzcordGameStatus=2
					If autoClose
						AAText(GraphicWidth / 2, RealGraphicHeight - 30, "PLEASE WAIT, THE GAME WILL PROCEED AUTOMATICALLY", True, True)
					Else
						AAText(GraphicWidth / 2, RealGraphicHeight - 30, "PRESS ANY KEY TO CONTINUE TO GAME", True, True)
					EndIf
			EndIf
		Else
			AAText(GraphicWidth / 2, RealGraphicHeight - 30, "Precaching: "+LoadingWhatAsset, True, True)
			FlushKeys()
			FlushMouse()
		EndIf
		
		If BorderlessWindowed Then
			If (RealGraphicWidth<>GraphicWidth) Or (RealGraphicHeight<>GraphicHeight) Then
				SetBuffer TextureBuffer(fresize_texture)
				ClsColor 0,0,0 : Cls
				CopyRect 0,0,GraphicWidth,GraphicHeight,1024-GraphicWidth/2,1024-GraphicHeight/2,BackBuffer(),TextureBuffer(fresize_texture)
				SetBuffer BackBuffer()
				ClsColor 0,0,0 : Cls
				;ScaleRender(0,0,2050.0 / Float(GraphicWidth) * AspectRatioRatio, 2050.0 / Float(GraphicWidth) * AspectRatioRatio)
				ScaleRender(0,0,2050.0 / Max(GraphicWidth,GraphicHeight) * AspectRatioRatio, 2050.0 / Max(GraphicWidth,GraphicHeight) * AspectRatioRatio)
				;might want to replace Float(GraphicWidth) with Max(GraphicWidth,GraphicHeight) if portrait sizes cause issues
				;everyone uses landscape so it's probably a non-issue
			EndIf
		EndIf
		
		;not by any means a perfect solution
		;Not even proper gamma correction but it's a nice looking alternative that works in windowed mode
		;If In3dMenu=0
		;	If ScreenGamma>1.0 Then
		;		CopyRect 0,0,RealGraphicWidth,RealGraphicHeight,1024-RealGraphicWidth/2,1024-RealGraphicHeight/2,BackBuffer(),TextureBuffer(fresize_texture)
		;		EntityBlend fresize_image,1
		;		ClsColor 0,0,0 : Cls
		;		ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth))
		;		EntityFX fresize_image,1+32
		;		EntityBlend fresize_image,3
		;		EntityAlpha fresize_image,ScreenGamma-1.0
		;		ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth))
		;	ElseIf ScreenGamma<1.0 Then ;todo: maybe optimize this if it's too slow, alternatively give players the option to disable gamma
		;		CopyRect 0,0,RealGraphicWidth,RealGraphicHeight,1024-RealGraphicWidth/2,1024-RealGraphicHeight/2,BackBuffer(),TextureBuffer(fresize_texture)
		;		EntityBlend fresize_image,1
		;		ClsColor 0,0,0 : Cls
		;		ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth))
		;		EntityFX fresize_image,1+32
		;		EntityBlend fresize_image,2
		;		EntityAlpha fresize_image,1.0
		;		SetBuffer TextureBuffer(fresize_texture2)
		;		ClsColor 255*ScreenGamma,255*ScreenGamma,255*ScreenGamma
		;		Cls
		;		SetBuffer BackBuffer()
		;		ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth))
		;		SetBuffer(TextureBuffer(fresize_texture2))
		;		ClsColor 0,0,0
		;		Cls
		;		SetBuffer(BackBuffer())
		;	EndIf
		;EndIf
		EntityFX fresize_image,1
		EntityBlend fresize_image,1
		EntityAlpha fresize_image,1.0
		
		Flip False
		
		firstloop = False
		If (percent <> 100 Or autoClose=True) Then Exit
		
	Until (GetKey()<>0 Or MouseHit(1))
	CatchErrors("DrawLoading")
End Function

Function UpdateLoading()
	If CurrentLoadingPercent > 0 And CurrentLoadingPercent < 100
		DrawLoading(CurrentLoadingPercent,LoadingFast)
	EndIf
End Function

Function rInput$(aString$)
	Local value% = GetKey()
	Local length% = Len(aString$)
	
	If value = 8 Then
		value = 0
		If length > 0 Then aString$ = Left(aString, length - 1)
	EndIf
	
	If value = 13 Or value = 0 Then
		Return aString$
	ElseIf value > 0 And value < 7 Or value > 26 And value < 32 Or value = 9
		Return aString$
	Else
		aString$ = aString$ + Chr(value)
		Return aString$
	End If
End Function

Function InputBox$(x%, y%, width%, height%, Txt$, ID% = 0)
	;TextBox(x,y,width,height,Txt$)
	Color (255, 255, 255)
	DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x, y, width, height)
	;Rect(x, y, width, height)
	Color (0, 0, 0)
	
	Local MouseOnBox% = False
	If MouseOn(x, y, width, height) Then
		Color(50, 50, 50)
		MouseOnBox = True
		If MouseHit1 Then SelectedInputBox = ID : FlushKeys
	EndIf
	
	Rect(x + 2, y + 2, width - 4, height - 4)
	Color (255, 255, 255)	
	
	If (Not MouseOnBox) And MouseHit1 And SelectedInputBox = ID Then SelectedInputBox = 0
	
	If SelectedInputBox = ID Then
		Txt = rInput(Txt)
		If (MilliSecs2() Mod 800) < 400 Then Rect (x + width / 2 + AAStringWidth(Txt) / 2 + 2, y + height / 2 - 5, 2, 12)
	EndIf	
	
	AAText(x + width / 2, y + height / 2, Txt, True, True)
	
	Return Txt
End Function

Function DrawFrame(x%, y%, width%, height%, xoffset%=0, yoffset%=0)
	Color 255, 255, 255
	DrawTiledImageRect(MenuWhite, xoffset, (y Mod 256), 512, 512, x, y, width, height)
	
	DrawTiledImageRect(MenuBlack, yoffset, (y Mod 256), 512, 512, x+3*MenuScale, y+3*MenuScale, width-6*MenuScale, height-6*MenuScale)	
End Function

Function DrawButton%(x%, y%, width%, height%, txt$, bigfont% = True, waitForMouseUp%=False, usingAA%=True)
	Local clicked% = False
	
	DrawFrame (x, y, width, height)
	If MouseOn(x, y, width, height) Then
		Color(30, 30, 30)
		If (MouseHit1 And (Not waitForMouseUp)) Or (MouseUp1 And waitForMouseUp) Then 
			clicked = True
			PlaySound_Strict(ButtonSFX)
		EndIf
		Rect(x + 4, y + 4, width - 8, height - 8)	
	Else
		Color(0, 0, 0)
	EndIf
	
	Color (255, 255, 255)
	If usingAA Then
		If bigfont Then AASetFont Font2 Else AASetFont Font1
		AAText(x + width / 2, y + height / 2, txt, True, True)
	Else
		If bigfont Then SetFont Font2 Else SetFont Font1
		Text(x + width / 2, y + height / 2, txt, True, True)
	EndIf
	
	Return clicked
End Function

Function DrawButton2%(x%, y%, width%, height%, txt$, bigfont% = True)
	Local clicked% = False
	
	DrawFrame (x, y, width, height)
	Local hit% = MouseHit(1)
	If MouseOn(x, y, width, height) Then
		Color(30, 30, 30)
		If hit Then clicked = True : PlaySound_Strict(ButtonSFX)
		Rect(x + 4, y + 4, width - 8, height - 8)	
	Else
		Color(0, 0, 0)
	EndIf
	
	Color (255, 255, 255)
	If bigfont Then SetFont Font2 Else SetFont Font1
	Text(x + width / 2, y + height / 2, txt, True, True)
	
	Return clicked
End Function

Function DrawDropdown%(x%, y%, value%, id%, txt1$="", txt2$="", txt3$="", txt4$="", txt5$="", txt6$="", txt7$="", txt8$="")
    Local currDrop.MenuDropdown
    Local md.MenuDropdown
    Local buttonexists%=False
    Local i%
	
    For md = Each MenuDropdown
        If md\x=x And md\y=y
            buttonexists=True
            Exit
        EndIf
    Next
    If (Not buttonexists)
        md = New MenuDropdown
        md\x = x
        md\y = y
        md\value = value
        md\ID = id
        md\txt1 = txt1
        md\txt2 = txt2
        md\txt3 = txt3
        md\txt4 = txt4
        md\txt5 = txt5
		md\txt6 = txt6
		md\txt7 = txt7
		md\txt8 = txt8
        If txt8 = "" Then
			If txt7 = "" Then
				If txt6 = "" Then
					If txt5 = "" Then
						If txt4 = "" Then
							If txt3 = "" Then
								If txt2 = "" Then
									If txt1 = "" Then
										txt1 = "Unavailable"
										md\size = 1
									Else
										md\size = 1
									EndIf
								Else
									md\size = 2
								EndIf
							Else
								md\size = 3
							EndIf
						Else
							md\size = 4
						EndIf
					Else
						md\size = 5
					EndIf
				Else
					md\size = 6
				EndIf
			Else
				md\size = 7
			EndIf
		Else
			md\size = 8
		EndIf
    Else
        currDrop = md
        currDrop\value = value
    EndIf
	
    If SelectedInputBox = md\ID
        If DrawButton(md\x + (120 - FRAME_THICK) * MenuScale, md\y, 30 * MenuScale, 30 * MenuScale, "-", False) Then
            SelectedInputBox = 0
        EndIf
        For i = 0 To md\size-1
            If MouseOn(md\x + 5.5 * MenuScale, md\y + (20 - FRAME_THICK + 20 + 20 * i) * MenuScale, 137 * MenuScale, 20 * MenuScale) Then
                If MouseHit1 Then
                    value = i
                    MouseHit1 = False
                    SelectedInputBox = 0
                    PlaySound_Strict(ButtonSFX)
                EndIf
            EndIf
        Next
    Else
        If DrawButton(md\x + (120 - FRAME_THICK) * MenuScale, md\y, 30 * MenuScale, 30 * MenuScale, "+", False) Then
            SelectedInputBox = md\ID
        EndIf
    EndIf
	
    Return value
End Function

Function DrawLangDropdown$(x%, y%, currStr$, id%, dir$)
    Local currDrop.MenuDropdown
    Local md.MenuDropdown
    Local buttonexists%=False
    Local i%
	
	Local txt1$="",txt2$="",txt3$="",txt4$="",txt5$="",txt6$="",txt7$="",txt8$=""
	
	Local value%=-1
	
	Local entryCount%=0
	
	If (FileType(dir) = 2)
		Local tmpDir = ReadDir(dir)
		Repeat
			Local tmpFile$=NextFile(tmpDir)
			
			If tmpFile="" Then Exit
			If tmpFile="." Or tmpFile=".." Then
				;ass
			Else
				If FileType(dir+"\"+tmpFile) = 2 Then
					entryCount=entryCount+1
					If entryCount > 8 Then Exit
					
					Select entryCount
						Case 1
							txt1=tmpFile
						Case 2
							txt2=tmpFile
						Case 3
							txt3=tmpFile
						Case 4
							txt4=tmpFile
						Case 5
							txt5=tmpFile
						Case 6
							txt6=tmpFile
						Case 7
							txt7=tmpFile
						Case 8
							txt8=tmpFile
					End Select
					
					If tmpFile=currStr Then value=entryCount-1
				EndIf
			EndIf
		Forever
		CloseDir tmpDir
	EndIf
	
	If value=-1 Then
		txt1="Error"
		txt2=""
		txt3=""
		txt4=""
		txt5=""
		txt6=""
		txt7=""
		txt8=""
		value=0
	EndIf
	
    For md = Each MenuDropdown
        If md\x=x And md\y=y
            buttonexists=True
            Exit
        EndIf
    Next
    If (Not buttonexists)
        md = New MenuDropdown
        md\x = x
        md\y = y
        md\value = value
        md\ID = id
        md\txt1 = txt1
        md\txt2 = txt2
        md\txt3 = txt3
        md\txt4 = txt4
        md\txt5 = txt5
		md\txt6 = txt6
		md\txt7 = txt7
		md\txt8 = txt8
		If txt8 = "" Then
			If txt7 = "" Then
				If txt6 = "" Then
					If txt5 = "" Then
						If txt4 = "" Then
							If txt3 = "" Then
								If txt2 = "" Then
									If txt1 = "" Then
										txt1 = "Unavailable"
										md\size = 1
									Else
										md\size = 1
									EndIf
								Else
									md\size = 2
								EndIf
							Else
								md\size = 3
							EndIf
						Else
							md\size = 4
						EndIf
					Else
						md\size = 5
					EndIf
				Else
					md\size = 6
				EndIf
			Else
				md\size = 7
			EndIf
		Else
			md\size = 8
		EndIf
    Else
        currDrop = md
        currDrop\value = value
    EndIf
	
    If SelectedInputBox = md\ID
        If DrawButton(md\x + (120 - FRAME_THICK) * MenuScale, md\y, 30 * MenuScale, 30 * MenuScale, "-", False) Then
            SelectedInputBox = 0
        EndIf
        For i = 0 To md\size-1
            If MouseOn(md\x + 5.5 * MenuScale, md\y + (20 - FRAME_THICK + 20 + 20 * i) * MenuScale, 137 * MenuScale, 20 * MenuScale) Then
                If MouseHit1 Then
                    value = i
                    MouseHit1 = False
                    SelectedInputBox = 0
                    PlaySound_Strict(ButtonSFX)
                EndIf
            EndIf
        Next
    Else
        If DrawButton(md\x + (120 - FRAME_THICK) * MenuScale, md\y, 30 * MenuScale, 30 * MenuScale, "+", False) Then
            SelectedInputBox = md\ID
        EndIf
    EndIf
	
	Local retText$
	
	Select value
		Case 0
			retText=md\txt1
		Case 1
			retText=md\txt2
		Case 2
			retText=md\txt3
		Case 3
			retText=md\txt4
		Case 4
			retText=md\txt5
		Case 5
			retText=md\txt6
		Case 6
			retText=md\txt7
		Case 7
			retText=md\txt8
	End Select
	
	If retText="Error" Then retText=""
	
	Return retText
End Function

Function DrawTick%(x%, y%, selected%, locked% = False)
	Local width% = 20 * MenuScale, height% = 20 * MenuScale
	
	Color (255, 255, 255)
	DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x, y, width, height)
	;Rect(x, y, width, height)
	
	Local Highlight% = MouseOn(x, y, width, height) And (Not locked)
	
	If Highlight Then
		Color(50, 50, 50)
		If MouseHit1 Then selected = (Not selected) : PlaySound_Strict (ButtonSFX)
	Else
		Color(0, 0, 0)		
	End If
	
	Rect(x + 2, y + 2, width - 4, height - 4)
	
	If selected Then
		If Highlight Then
			Color 255,255,255
		Else
			Color 200,200,200
		EndIf
		DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x + 4, y + 4, width - 8, height - 8)
		;Rect(x + 4, y + 4, width - 8, height - 8)
	EndIf
	
	Color 255, 255, 255
	
	Return selected
End Function

Function SlideBar#(x%, y%, width%, value#)
	
	If MouseDown1 And OnSliderID=0 Then
		If ScaledMouseX() >= x And ScaledMouseX() <= x + width + 14 And ScaledMouseY() >= y And ScaledMouseY() <= y + 20 Then
			value = Min(Max((ScaledMouseX() - x) * 100 / width, 0), 100)
		EndIf
	EndIf
	
	Color 255,255,255
	Rect(x, y, width + 14, 20,False)
	
	DrawImage(BlinkMeterIMG, x + width * value / 100.0 +3, y+3)
	
	Color 170,170,170 
	AAText (x - 50 * MenuScale, y + 4*MenuScale, "LOW")					
	AAText (x + width + 38 * MenuScale, y+4*MenuScale, "HIGH")	
	
	Return value
	
End Function

;Function FolderDropDown$(x%, y%, locked% = False, searchPath$)
;	Local width% = 80 * MenuScale, height% = 30 * MenuScale
;	Local selected$
;	Local Dir$
;	Local numOptions%
;	Local tempX% = x
;	Local tempY% = y
;	Local file$
;	
;	Color (255, 255, 255)
;	DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x, y, width, height)
;	Rect(x, y, width, height)
;	
;	Local Highlight% = MouseOn(x, y, width, height) And (Not locked)
;	
;	If Highlight Then
;		Color(50, 50, 50)
;		If MouseHit1 Then
;			Dir = ReadDir(searchPath)
;			Repeat
;				file = NextFile(Dir)
;				If file="" Then Exit
;				If FileType(searchPath + "\" + file) = 2 Then
;					numOptions = numOptions + 1
;					AAText(tempX + width / 2, tempY + height / 2, file, True, True)
;					tempX = tempX + 40
;					tempY = tempY + 40
;				EndIf
;			Forever
;		EndIf
;	Else
;		Color(0, 0, 0)		
;	End If
;	
;	Rect(x + 2, y + 2, width - 4, height - 4)
;	
;	If selected Then
;		If Highlight Then
;			Color 255,255,255
;		Else
;			Color 200,200,200
;		EndIf
;		DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x + 4, y + 4, width - 8, height - 8)
;		;Rect(x + 4, y + 4, width - 8, height - 8)
;	EndIf
;	
;	Color 255, 255, 255
;	
;	CloseDir(searchPath)
;	
;	Return selected
;End Function




Function RowText(A$, X, Y, W, H, align% = 0, Leading#=1)
	;Display A$ starting at X,Y - no wider than W And no taller than H (all in pixels).
	;Leading is optional extra vertical spacing in pixels
	
	If H<1 Then H=2048
	
	Local LinesShown = 0
	Local Height = AAStringHeight(A$) + Leading
	Local b$
	
	While Len(A) > 0
		Local space = Instr(A$, " ")
		If space = 0 Then space = Len(A$)
		Local temp$ = Left(A$, space)
		Local trimmed$ = Trim(temp) ;we might ignore a final space 
		Local extra = 0 ;we haven't ignored it yet
		;ignore final space If doing so would make a word fit at End of Line:
		If (AAStringWidth (b$ + temp$) > W) And (AAStringWidth (b$ + trimmed$) <= W) Then
			temp = trimmed
			extra = 1
		EndIf
		
		If AAStringWidth (b$ + temp$) > W Then ;too big, so Print what will fit
			If align Then
				AAText(X + W / 2 - (AAStringWidth(b) / 2), LinesShown * Height + Y, b)
			Else
				AAText(X, LinesShown * Height + Y, b)
			EndIf			
			
			LinesShown = LinesShown + 1
			b$=""
		Else ;append it To b$ (which will eventually be printed) And remove it from A$
			b$ = b$ + temp$
			A$ = Right(A$, Len(A$) - (Len(temp$) + extra))
		EndIf
		
		If ((LinesShown + 1) * Height) > H Then Exit ;the Next Line would be too tall, so leave
	Wend
	
	If (b$ <> "") And((LinesShown + 1) <= H) Then
		If align Then
			AAText(X + W / 2 - (AAStringWidth(b) / 2), LinesShown * Height + Y, b) ;Print any remaining Text If it'll fit vertically
		Else
			AAText(X, LinesShown * Height + Y, b) ;Print any remaining Text If it'll fit vertically
		EndIf
	EndIf
	
End Function

Function RowText2(A$, X, Y, W, H, align% = 0, Leading#=1)
	;Display A$ starting at X,Y - no wider than W And no taller than H (all in pixels).
	;Leading is optional extra vertical spacing in pixels
	
	If H<1 Then H=2048
	
	Local LinesShown = 0
	Local Height = StringHeight(A$) + Leading
	Local b$
	
	While Len(A) > 0
		Local space = Instr(A$, " ")
		If space = 0 Then space = Len(A$)
		Local temp$ = Left(A$, space)
		Local trimmed$ = Trim(temp) ;we might ignore a final space 
		Local extra = 0 ;we haven't ignored it yet
		;ignore final space If doing so would make a word fit at End of Line:
		If (StringWidth (b$ + temp$) > W) And (StringWidth (b$ + trimmed$) <= W) Then
			temp = trimmed
			extra = 1
		EndIf
		
		If StringWidth (b$ + temp$) > W Then ;too big, so Print what will fit
			If align Then
				Text(X + W / 2 - (StringWidth(b) / 2), LinesShown * Height + Y, b)
			Else
				Text(X, LinesShown * Height + Y, b)
			EndIf
			
			LinesShown = LinesShown + 1
			b$=""
		Else ;append it To b$ (which will eventually be printed) And remove it from A$
			b$ = b$ + temp$
			A$ = Right(A$, Len(A$) - (Len(temp$) + extra))
		EndIf
		
		If ((LinesShown + 1) * Height) > H Then Exit ;the Next Line would be too tall, so leave
	Wend
	
	If (b$ <> "") And((LinesShown + 1) <= H) Then
		If align Then
			Text(X + W / 2 - (StringWidth(b) / 2), LinesShown * Height + Y, b) ;Print any remaining Text If it'll fit vertically
		Else
			Text(X, LinesShown * Height + Y, b) ;Print any remaining Text If it'll fit vertically
		EndIf
	EndIf
	
End Function

Function GetLineAmount(A$, W, H, Leading#=1)
	;Display A$ starting at X,Y - no wider than W And no taller than H (all in pixels).
	;Leading is optional extra vertical spacing in pixels
	
	If H<1 Then H=2048
	
	Local LinesShown = 0
	Local Height = AAStringHeight(A$) + Leading
	Local b$
	
	While Len(A) > 0
		Local space = Instr(A$, " ")
		If space = 0 Then space = Len(A$)
		Local temp$ = Left(A$, space)
		Local trimmed$ = Trim(temp) ;we might ignore a final space 
		Local extra = 0 ;we haven't ignored it yet
		;ignore final space If doing so would make a word fit at End of Line:
		If (AAStringWidth (b$ + temp$) > W) And (AAStringWidth (b$ + trimmed$) <= W) Then
			temp = trimmed
			extra = 1
		EndIf
		
		If AAStringWidth (b$ + temp$) > W Then ;too big, so Print what will fit
			
			LinesShown = LinesShown + 1
			b$=""
		Else ;append it To b$ (which will eventually be printed) And remove it from A$
			b$ = b$ + temp$
			A$ = Right(A$, Len(A$) - (Len(temp$) + extra))
		EndIf
		
		If ((LinesShown + 1) * Height) > H Then Exit ;the Next Line would be too tall, so leave
	Wend
	
	Return LinesShown+1
	
End Function

Function GetLineAmount2(A$, W, H, Leading#=1)
	;Display A$ starting at X,Y - no wider than W And no taller than H (all in pixels).
	;Leading is optional extra vertical spacing in pixels
	
	If H<1 Then H=2048
	
	Local LinesShown = 0
	Local Height = StringHeight(A$) + Leading
	Local b$
	
	While Len(A) > 0
		Local space = Instr(A$, " ")
		If space = 0 Then space = Len(A$)
		Local temp$ = Left(A$, space)
		Local trimmed$ = Trim(temp) ;we might ignore a final space 
		Local extra = 0 ;we haven't ignored it yet
		;ignore final space If doing so would make a word fit at End of Line:
		If (StringWidth (b$ + temp$) > W) And (StringWidth (b$ + trimmed$) <= W) Then
			temp = trimmed
			extra = 1
		EndIf
		
		If StringWidth (b$ + temp$) > W Then ;too big, so Print what will fit
			
			LinesShown = LinesShown + 1
			b$=""
		Else ;append it To b$ (which will eventually be printed) And remove it from A$
			b$ = b$ + temp$
			A$ = Right(A$, Len(A$) - (Len(temp$) + extra))
		EndIf
		
		If ((LinesShown + 1) * Height) > H Then Exit ;the Next Line would be too tall, so leave
	Wend
	
	Return LinesShown+1
	
End Function

Function LimitText%(txt$, x%, y%, width%, usingAA%=True)
	Local TextLength%
	Local UnFitting%
	Local LetterWidth%
	If usingAA Then
		If txt = "" Or width = 0 Then Return 0
		TextLength = AAStringWidth(txt)
		UnFitting = TextLength - width
		If UnFitting <= 0 Then ;mahtuu
			AAText(x, y, txt)
		Else ;ei mahdu
			LetterWidth = TextLength / Len(txt)
			
			AAText(x, y, Left(txt, Max(Len(txt) - UnFitting / LetterWidth - 4, 1)) + "...")
		End If
	Else
		If txt = "" Or width = 0 Then Return 0
		TextLength = StringWidth(txt)
		UnFitting = TextLength - width
		If UnFitting <= 0 Then ;mahtuu
			Text(x, y, txt)
		Else ;ei mahdu
			LetterWidth = TextLength / Len(txt)
			
			Text(x, y, Left(txt, Max(Len(txt) - UnFitting / LetterWidth - 4, 1)) + "...")
		End If
	EndIf
End Function

Function DrawTooltip(message$)
	Local scale# = GraphicHeight/768.0
	
	Local width = (AAStringWidth(message$))+20*MenuScale
	
	Color 25,25,25
	Rect(ScaledMouseX()+20,ScaledMouseY(),width,19*scale,True)
	Color 150,150,150
	Rect(ScaledMouseX()+20,ScaledMouseY(),width,19*scale,False)
	AASetFont Font1
	AAText(ScaledMouseX()+(20*MenuScale)+(width/2),ScaledMouseY()+(12*MenuScale), message$, True, True)
End Function

Global QuickLoadPercent% = -1
Global QuickLoadPercent_DisplayTimer# = 0
Global QuickLoad_CurrEvent.Events

Function DrawQuickLoading()
	
	If QuickLoadPercent > -1
		MidHandle QuickLoadIcon
		DrawImage QuickLoadIcon,GraphicWidth-150,GraphicHeight-150
		Color 255,255,255
		AASetFont Font1
		AAText GraphicWidth-100,GraphicHeight-90,"LOADING: "+QuickLoadPercent+"%",1
		If QuickLoadPercent > 99
			If QuickLoadPercent_DisplayTimer < 70
				QuickLoadPercent_DisplayTimer# = Min(QuickLoadPercent_DisplayTimer+FPSfactor,70)
			Else
				QuickLoadPercent = -1
			EndIf
		EndIf
		QuickLoadEvents()
	Else
		QuickLoadPercent = -1
		QuickLoadPercent_DisplayTimer# = 0
		QuickLoad_CurrEvent = Null
	EndIf
	
End Function

Function DrawOptionsTooltip(x%,y%,width%,height%,option$,value#=0,ingame%=False)
	Local fx# = x+6*MenuScale
	Local fy# = y+6*MenuScale
	Local fw# = width-12*MenuScale
	Local fh# = height-12*MenuScale
	Local lines% = 0, lines2% = 0
	Local txt$ = ""
	Local txt2$ = "", R% = 0, G% = 0, B% = 0
	Local usetestimg% = False, extraspace% = 0
	
	AASetFont Font1
	Color 255,255,255
	Select Lower(option$)
		;Graphic options
			;[Block]
		Case "bump"
			txt = Chr(34)+"Bump mapping"+Chr(34)+" is used to simulate bumps and dents by distorting the lightmaps."
			txt2 = "This option cannot be changed in-game."
			R = 255
		Case "vsync"
			txt = Chr(34)+"Vertical sync"+Chr(34)+" waits for the display to finish its current refresh cycle before calculating the next frame, preventing issues such as "
			txt = txt + "screen tearing. This ties the game's frame rate to your display's refresh rate and may cause some input lag."
		Case "antialias"
			txt = Chr(34)+"Anti-Aliasing"+Chr(34)+" is used to smooth the rendered image before displaying in order to reduce aliasing around the edges of models."
			txt2 = "This option only takes effect in fullscreen."
			R = 255
		Case "roomlights"
			txt = "Toggles the artificial lens flare effect generated over specific light sources."
		Case "gamma"
			txt = Chr(34)+"Gamma correction"+Chr(34)+" is used to achieve a good brightness factor to balance out your display's gamma if the game appears either too dark or bright. "
			txt = txt + "Setting it too high or low can cause the graphics to look less detailed."
			R = 255
			G = 255
			B = 255
			txt2 = "Current value: "+Int(value*100)+"% (default is 100%)"
		Case "texquality"
			txt = Chr(34)+"Texture LOD Bias"+Chr(34)+" affects the distance at which texture detail will change to prevent aliasing. Change this option if textures flicker or look too blurry."
		Case "particleamount"
			txt = "Determines the amount of particles that can be rendered per tick."
			Select value
				Case 0
					R = 255
					txt2 = "Only smoke emitters will produce particles."
				Case 1
					R = 255
					G = 255
					txt2 = "Only a few particles will be rendered per tick."
				Case 2
					G = 255
					txt2 = "All particles are rendered."
			End Select
		Case "vram"
			txt = "Textures that are stored in the Video-RAM will load faster, but this also has negative effects on the texture quality as well."
			txt2 = "This option cannot be changed in-game."
			R = 255
		Case "nocursor"
			txt = "Use The Computer's Default Cursor instead of the game's custom cursor."
		Case "furri"
			txt = "Enable if you dare..."
			;[End Block]
		;Sound options
			;[Block]
		Case "musicvol"
			txt = "Adjusts the volume of background music. Sliding the bar fully to the left will mute all music."
			R = 255
			G = 255
			B = 255
			txt2 = "Current value: "+Int(value*100)+"% (default is 50%)"
		Case "soundvol"
			txt = "Adjusts the volume of sound effects. Sliding the bar fully to the left will mute all sounds."
			R = 255
			G = 255
			B = 255
			txt2 = "Current value: "+Int(value*100)+"% (default is 100%)"
		Case "sfxautorelease"
			txt = Chr(34)+"Sound auto-release"+Chr(34)+" will free a sound from memory if it not used after 5 seconds. Prevents memory allocation issues."
			R = 255
			txt2 = "This option cannot be changed in-game."
		Case "usertrack"
			txt = "Toggles the ability to play custom tracks over channel 1 of the radio. These tracks are loaded from the " + Chr(34) + "SFX\Radio\UserTracks\" + Chr(34)
			txt = txt + " directory. Press " + Chr(34) + "1" + Chr(34) + " when the radio is selected to change track."
			R = 255
			txt2 = "This option cannot be changed in-game."
		Case "usertrackmode"
			txt = "Sets the playing mode for the custom tracks. "+Chr(34)+"Repeat"+Chr(34)+" plays every file in alphabetical order. "+Chr(34)+"Random"+Chr(34)+" chooses the "
			txt = txt + "next track at random."
			R = 255
			G = 255
			txt2 = "Note that the random mode does not prevent previously played tracks from repeating."
		Case "usertrackscan"
			txt = "Re-checks the user tracks directory for any new or removed sound files."
		Case "subtitles"
			txt = "Enables closed captions that display what characters are saying."
		Case "subtitlelang"
			txt = "The language of the closed captions should they be enabled."
			;[End Block]
		;Control options	
			;[Block]
		Case "mousesensitivity"
			txt = "Adjusts the speed of the mouse pointer."
			R = 255
			G = 255
			B = 255
			txt2 = "Current value: "+Int((0.5+value)*100)+"% (default is 50%)"
		Case "mouseinvert"
			txt = Chr(34)+"Invert mouse Y-axis"+Chr(34)+" is self-explanatory."
		Case "mousesmoothing"
			txt = "Adjusts the amount of smoothing of the mouse pointer."
			R = 255
			G = 255
			B = 255
			txt2 = "Current value: "+Int(value*100)+"% (default is 100%)"
		Case "controls"
			txt = "Configure the in-game control scheme."
			;[End Block]
		;Advanced options	
			;[Block]
		Case "hud"
			txt = "Display the blink and stamina meters."
		Case "consoleenable"
			txt = "Toggles the use of the developer console. Can be used in-game by pressing " + KeyName(KEY_CONSOLE) + "."
		Case "consoleerror"
			txt = Chr(34)+"Open console on error"+Chr(34)+" is self-explanatory."
		Case "achpopup"
			txt = "Displays a pop-up notification when an achievement is unlocked."
		Case "showfps"
			txt = "Displays the frames per second counter at the top left-hand corner."
		Case "framelimit"
			txt = "Limits the frame rate that the game can run at to a desired value."
			If value > 0 And value < 60
				R = 255
				G = 255
				txt2 = "Usually, 60 FPS or higher is preferred. If you are noticing excessive stuttering at this setting, try lowering it to make your framerate more consistent."
			EndIf
		Case "antialiastext"
			txt = Chr(34)+"Antialiased text"+Chr(34)+" smooths out the text before displaying. Makes text easier to read at high resolutions."
		Case "uselauncher"
			txt = "Toggles the launcher window on startup which allows you to configure your resolution along with other settings that you cannot configure in-game."
		Case "menu3d"
			txt = "Toggles the 3D Menu."
			;[End Block]
	End Select
	
	lines% = GetLineAmount(txt,fw,fh)
	If usetestimg
		extraspace = 210*MenuScale
	EndIf
	If txt2$ = ""
		DrawFrame(x,y,width,((AAStringHeight(txt)*lines)+(10+lines)*MenuScale)+extraspace)
	Else
		lines2% = GetLineAmount(txt2,fw,fh)
		DrawFrame(x,y,width,(((AAStringHeight(txt)*lines)+(10+lines)*MenuScale)+(AAStringHeight(txt2)*lines2)+(10+lines2)*MenuScale)+extraspace)
	EndIf
	RowText(txt,fx,fy,fw,fh)
	If txt2$ <> ""
		Color R,G,B
		RowText(txt2,fx,(fy+(AAStringHeight(txt)*lines)+(5+lines)*MenuScale),fw,fh)
	EndIf
	If usetestimg
		MidHandle Menu_TestIMG
		If txt2$ = ""
			DrawImage Menu_TestIMG,x+(width/2),y+100*MenuScale+((AAStringHeight(txt)*lines)+(10+lines)*MenuScale)
		Else
			DrawImage Menu_TestIMG,x+(width/2),y+100*MenuScale+(((AAStringHeight(txt)*lines)+(10+lines)*MenuScale)+(AAStringHeight(txt2)*lines2)+(10+lines2)*MenuScale)
		EndIf
	EndIf
	
End Function

Function DrawMapCreatorTooltip(x%,y%,width%,height%,mapname$)
	Local fx# = x+6*MenuScale
	Local fy# = y+6*MenuScale
	Local fw# = width-12*MenuScale
	Local fh# = height-12*MenuScale
	Local lines% = 0
	
	AASetFont Font1
	Color 255,255,255
	
	Local txt$[5]
	If Right(mapname,6)="cbmap2" Then
		txt[0] = Left(mapname$,Len(mapname$)-7)
		Local f% = OpenFile("Map Creator\Maps\"+mapname$)
		
		Local author$ = ReadLine(f)
		Local descr$ = ReadLine(f)
		ReadByte(f)
		ReadByte(f)
		Local ramount% = ReadInt(f)
		If ReadInt(f) > 0 Then
			Local hasForest% = True
		Else
			hasForest% = False
		EndIf
		If ReadInt(f) > 0 Then
			Local hasMT% = True
		Else
			hasMT% = False
		EndIf
		
		CloseFile f%
	Else
		txt[0] = Left(mapname$,Len(mapname$)-6)
		author$ = "[Unspecified]"
		descr$ = "[No description]"
		ramount% = 0
		hasForest% = False
		hasMT% = False
	EndIf
	txt[1] = "Made by: "+author$
	txt[2] = "Description: "+descr$
	If ramount > 0 Then
		txt[3] = "Room amount: "+ramount
	Else
		txt[3] = "Room amount: [Illegal]"
	EndIf
	If hasForest Then
		txt[4] = "Has custom forest: ;Yes"
	Else
		txt[4] = "Has custom forest: No"
	EndIf
	If hasMT Then
		txt[5] = "Has custom maintenance tunnel: Yes"
	Else
		txt[5] = "Has custom maintenance tunnel: No"
	EndIf
	
	lines% = GetLineAmount(txt[2],fw,fh)
	DrawFrame(x,y,width,(AAStringHeight(txt[0])*6)+AAStringHeight(txt[2])*lines+5*MenuScale)
	
	Color 255,255,255
	AAText(fx,fy,txt[0])
	AAText(fx,fy+AAStringHeight(txt[0]),txt[1])
	RowText(txt[2],fx,fy+(AAStringHeight(txt[0])*2),fw,fh)
	AAText(fx,fy+((AAStringHeight(txt[0])*2)+AAStringHeight(txt[2])*lines+5*MenuScale),txt[3])
	AAText(fx,fy+((AAStringHeight(txt[0])*3)+AAStringHeight(txt[2])*lines+5*MenuScale),txt[4])
	AAText(fx,fy+((AAStringHeight(txt[0])*4)+AAStringHeight(txt[2])*lines+5*MenuScale),txt[5])
	
End Function

Function ChangeMenu_TestIMG(change$)
	
	If Menu_TestIMG <> 0 Then FreeImage Menu_TestIMG
	AmbientLightRoomTex% = CreateTexture(2,2,257)
	TextureBlend AmbientLightRoomTex,5
	SetBuffer(TextureBuffer(AmbientLightRoomTex))
	ClsColor 0,0,0
	Cls
	SetBuffer BackBuffer()
	Menu_TestIMG = Create3DIcon(200,200,"GFX\map\room3z3_opt.rmesh",0,-0.75,1,0,0,0,menuroomscale#,menuroomscale#,menuroomscale#,True)
	ScaleImage Menu_TestIMG,MenuScale,MenuScale
	MaskImage Menu_TestIMG,255,0,255
	FreeTexture AmbientLightRoomTex : AmbientLightRoomTex = 0
	
	CurrMenu_TestIMG = change$
	
End Function

Global OnSliderID% = 0

Function Slider3(x%,y%,width%,value%,ID%,val1$,val2$,val3$)
	
	If MouseDown1 Then
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			OnSliderID = ID
		EndIf
	EndIf
	
	Color 200,200,200
	Rect(x,y,width+14,10,True)
	Rect(x,y-8,4,14,True)
	Rect(x+(width/2)+5,y-8,4,14,True)
	Rect(x+width+10,y-8,4,14,True)
	
	If ID = OnSliderID
		If (ScaledMouseX() <= x+8)
			value = 0
		ElseIf (ScaledMouseX() >= x+width/2) And (ScaledMouseX() <= x+(width/2)+8)
			value = 1
		ElseIf (ScaledMouseX() >= x+width)
			value = 2
		EndIf
		Color 0,255,0
		Rect(x,y,width+14,10,True)
	Else
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			Color 0,200,0
			Rect(x,y,width+14,10,False)
		EndIf
	EndIf
	
	If value = 0
		DrawImage(BlinkMeterIMG,x,y-8)
	ElseIf value = 1
		DrawImage(BlinkMeterIMG,x+(width/2)+3,y-8)
	Else
		DrawImage(BlinkMeterIMG,x+width+6,y-8)
	EndIf
	
	Color 170,170,170
	If value = 0
		AAText(x+2,y+10+MenuScale,val1,True)
	ElseIf value = 1
		AAText(x+(width/2)+7,y+10+MenuScale,val2,True)
	Else
		AAText(x+width+12,y+10+MenuScale,val3,True)
	EndIf
	
	Return value
	
End Function

Function Slider4(x%,y%,width%,value%,ID%,val1$,val2$,val3$,val4$)
	
	If MouseDown1 Then
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			OnSliderID = ID
		EndIf
	EndIf
	
	Color 200,200,200
	Rect(x,y,width+14,10,True)
	Rect(x,y-8,4,14,True) ;1
	Rect(x+(width*(1.0/3.0))+(10.0/3.0),y-8,4,14,True) ;2
	Rect(x+(width*(2.0/3.0))+(20.0/3.0),y-8,4,14,True) ;3
	Rect(x+width+10,y-8,4,14,True) ;4
	
	If ID = OnSliderID
		If (ScaledMouseX() <= x+8)
			value = 0
		ElseIf (ScaledMouseX() >= x+width*(1.0/3.0)) And (ScaledMouseX() <= x+width*(1.0/3.0)+8)
			value = 1
		ElseIf (ScaledMouseX() >= x+width*(2.0/3.0)) And (ScaledMouseX() <= x+width*(2.0/3.0)+8)
			value = 2
		ElseIf (ScaledMouseX() >= x+width)
			value = 3
		EndIf
		Color 0,255,0
		Rect(x,y,width+14,10,True)
	Else
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			Color 0,200,0
			Rect(x,y,width+14,10,False)
		EndIf
	EndIf
	
	If value = 0
		DrawImage(BlinkMeterIMG,x,y-8)
	ElseIf value = 1
		DrawImage(BlinkMeterIMG,x+width*(1.0/3.0)+2,y-8)
	ElseIf value = 2
		DrawImage(BlinkMeterIMG,x+width*(2.0/3.0)+4,y-8)
	Else
		DrawImage(BlinkMeterIMG,x+width+6,y-8)
	EndIf
	
	Color 170,170,170
	If value = 0
		AAText(x+2,y+10+MenuScale,val1,True)
	ElseIf value = 1
		AAText(x+width*(1.0/3.0)+2+(10.0/3.0),y+10+MenuScale,val2,True)
	ElseIf value = 2
		AAText(x+width*(2.0/3.0)+2+((10.0/3.0)*2),y+10+MenuScale,val3,True)
	Else
		AAText(x+width+12,y+10+MenuScale,val4,True)
	EndIf
	
	Return value
	
End Function

Function Slider5(x%,y%,width%,value%,ID%,val1$,val2$,val3$,val4$,val5$)
	
	If MouseDown1 Then
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			OnSliderID = ID
		EndIf
	EndIf
	
	Color 200,200,200
	Rect(x,y,width+14,10,True)
	Rect(x,y-8,4,14,True) ;1
	Rect(x+(width/4)+2.5,y-8,4,14,True) ;2
	Rect(x+(width/2)+5,y-8,4,14,True) ;3
	Rect(x+(width*0.75)+7.5,y-8,4,14,True) ;4
	Rect(x+width+10,y-8,4,14,True) ;5
	
	If ID = OnSliderID
		If (ScaledMouseX() <= x+8)
			value = 0
		ElseIf (ScaledMouseX() >= x+width/4) And (ScaledMouseX() <= x+(width/4)+8)
			value = 1
		ElseIf (ScaledMouseX() >= x+width/2) And (ScaledMouseX() <= x+(width/2)+8)
			value = 2
		ElseIf (ScaledMouseX() >= x+width*0.75) And (ScaledMouseX() <= x+(width*0.75)+8)
			value = 3
		ElseIf (ScaledMouseX() >= x+width)
			value = 4
		EndIf
		Color 0,255,0
		Rect(x,y,width+14,10,True)
	Else
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			Color 0,200,0
			Rect(x,y,width+14,10,False)
		EndIf
	EndIf
	
	If value = 0
		DrawImage(BlinkMeterIMG,x,y-8)
	ElseIf value = 1
		DrawImage(BlinkMeterIMG,x+(width/4)+1.5,y-8)
	ElseIf value = 2
		DrawImage(BlinkMeterIMG,x+(width/2)+3,y-8)
	ElseIf value = 3
		DrawImage(BlinkMeterIMG,x+(width*0.75)+4.5,y-8)
	Else
		DrawImage(BlinkMeterIMG,x+width+6,y-8)
	EndIf
	
	Color 170,170,170
	If value = 0
		AAText(x+2,y+10+MenuScale,val1,True)
	ElseIf value = 1
		AAText(x+(width/4)+4.5,y+10+MenuScale,val2,True)
	ElseIf value = 2
		AAText(x+(width/2)+7,y+10+MenuScale,val3,True)
	ElseIf value = 3
		AAText(x+(width*0.75)+9.5,y+10+MenuScale,val4,True)
	Else
		AAText(x+width+12,y+10+MenuScale,val5,True)
	EndIf
	
	Return value
	
End Function

Function Slider7(x%,y%,width%,value%,ID%,val1$,val2$,val3$,val4$,val5$,val6$,val7$)
	
	If MouseDown1 Then
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			OnSliderID = ID
		EndIf
	EndIf
	
	Color 200,200,200
	Rect(x,y,width+14,10,True)
	Rect(x,y-8,4,14,True) ;1
	Rect(x+(width*(1.0/6.0))+(10.0/6.0),y-8,4,14,True) ;2
	Rect(x+(width*(2.0/6.0))+(20.0/6.0),y-8,4,14,True) ;3
	Rect(x+(width*(3.0/6.0))+(30.0/6.0),y-8,4,14,True) ;4
	Rect(x+(width*(4.0/6.0))+(40.0/6.0),y-8,4,14,True) ;5
	Rect(x+(width*(5.0/6.0))+(50.0/6.0),y-8,4,14,True) ;6
	Rect(x+width+10,y-8,4,14,True) ;7
	
	If ID = OnSliderID
		If (ScaledMouseX() <= x+8)
			value = 0
		ElseIf (ScaledMouseX() >= x+(width*(1.0/6.0))) And (ScaledMouseX() <= x+(width*(1.0/6.0))+8)
			value = 1
		ElseIf (ScaledMouseX() >= x+(width*(2.0/6.0))) And (ScaledMouseX() <= x+(width*(2.0/6.0))+8)
			value = 2
		ElseIf (ScaledMouseX() >= x+(width*(3.0/6.0))) And (ScaledMouseX() <= x+(width*(3.0/6.0))+8)
			value = 3
		ElseIf (ScaledMouseX() >= x+(width*(4.0/6.0))) And (ScaledMouseX() <= x+(width*(4.0/6.0))+8)
			value = 4
		ElseIf (ScaledMouseX() >= x+(width*(5.0/6.0))) And (ScaledMouseX() <= x+(width*(5.0/6.0))+8)
			value = 5
		ElseIf (ScaledMouseX() >= x+width)
			value = 6
		EndIf
		Color 0,255,0
		Rect(x,y,width+14,10,True)
	Else
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			Color 0,200,0
			Rect(x,y,width+14,10,False)
		EndIf
	EndIf
	
	If value = 0
		DrawImage(BlinkMeterIMG,x,y-8)
	ElseIf value = 1
		DrawImage(BlinkMeterIMG,x+(width*(1.0/6.0))+1,y-8)
	ElseIf value = 2
		DrawImage(BlinkMeterIMG,x+(width*(2.0/6.0))+2,y-8)
	ElseIf value = 3
		DrawImage(BlinkMeterIMG,x+(width*(3.0/6.0))+3,y-8)
	ElseIf value = 4
		DrawImage(BlinkMeterIMG,x+(width*(4.0/6.0))+4,y-8)
	ElseIf value = 5
		DrawImage(BlinkMeterIMG,x+(width*(5.0/6.0))+5,y-8)
	Else
		DrawImage(BlinkMeterIMG,x+width+6,y-8)
	EndIf
	
	Color 170,170,170
	If value = 0
		AAText(x+2,y+10+MenuScale,val1,True)
	ElseIf value = 1
		AAText(x+(width*(1.0/6.0))+2+(10.0/6.0),y+10+MenuScale,val2,True)
	ElseIf value = 2
		AAText(x+(width*(2.0/6.0))+2+((10.0/6.0)*2),y+10+MenuScale,val3,True)
	ElseIf value = 3
		AAText(x+(width*(3.0/6.0))+2+((10.0/6.0)*3),y+10+MenuScale,val4,True)
	ElseIf value = 4
		AAText(x+(width*(4.0/6.0))+2+((10.0/6.0)*4),y+10+MenuScale,val5,True)
	ElseIf value = 5
		AAText(x+(width*(5.0/6.0))+2+((10.0/6.0)*5),y+10+MenuScale,val6,True)
	Else
		AAText(x+width+12,y+10+MenuScale,val7,True)
	EndIf
	
	Return value
	
End Function

Global OnBar%
Global ScrollBarY# = 0.0
Global ScrollMenuHeight# = 0.0

Function DrawScrollBar#(x, y, width, height, barx, bary, barwidth, barheight, bar#, dir = 0)
	;0 = vaakasuuntainen, 1 = pystysuuntainen
	
	Local MouseSpeedX = MouseXSpeed()
	Local MouseSpeedY = MouseYSpeed()
	
	Color(0, 0, 0)
	;Rect(x, y, width, height)
	Button(barx, bary, barwidth, barheight, "")
	
	If dir = 0 Then ;vaakasuunnassa
		If height > 10 Then
			Color 250,250,250
			Rect(barx + barwidth / 2, bary + 5*MenuScale, 2*MenuScale, barheight - 10)
			Rect(barx + barwidth / 2 - 3*MenuScale, bary + 5*MenuScale, 2*MenuScale, barheight - 10)
			Rect(barx + barwidth / 2 + 3*MenuScale, bary + 5*MenuScale, 2*MenuScale, barheight - 10)
		EndIf
	Else ;pystysuunnassa
		If width > 10 Then
			Color 250,250,250
			Rect(barx + 4*MenuScale, bary + barheight / 2, barwidth - 10*MenuScale, 2*MenuScale)
			Rect(barx + 4*MenuScale, bary + barheight / 2 - 3*MenuScale, barwidth - 10*MenuScale, 2*MenuScale)
			Rect(barx + 4*MenuScale, bary + barheight / 2 + 3*MenuScale, barwidth - 10*MenuScale, 2*MenuScale)
		EndIf
	EndIf
	
	If MouseX()>barx And MouseX()<barx+barwidth
		If MouseY()>bary And MouseY()<bary+barheight
			OnBar = True
		Else
			If (Not MouseDown1)
				OnBar = False
			EndIf
		EndIf
	Else
		If (Not MouseDown1)
			OnBar = False
		EndIf
	EndIf
	
	If MouseDown1
		If OnBar
			If dir = 0
				Return Min(Max(bar + MouseSpeedX / Float(width - barwidth), 0), 1)
			Else
				Return Min(Max(bar + MouseSpeedY / Float(height - barheight), 0), 1)
			EndIf
		EndIf
	EndIf
	
	Return bar
	
End Function

Function Button%(x,y,width,height,txt$, disabled%=False)
	Local Pushed = False
	
	Color 50, 50, 50
	If Not disabled Then 
		If MouseX() > x And MouseX() < x+width Then
			If MouseY() > y And MouseY() < y+height Then
				If MouseDown1 Then
					Pushed = True
					Color 50*0.6, 50*0.6, 50*0.6
				Else
					Color Min(50*1.2,255),Min(50*1.2,255),Min(50*1.2,255)
				EndIf
			EndIf
		EndIf
	EndIf
	
	If Pushed Then 
		Rect x,y,width,height
		Color 133,130,125
		Rect x+1*MenuScale,y+1*MenuScale,width-1*MenuScale,height-1*MenuScale,False	
		Color 10,10,10
		Rect x,y,width,height,False
		Color 250,250,250
		Line x,y+height-1*MenuScale,x+width-1*MenuScale,y+height-1*MenuScale
		Line x+width-1*MenuScale,y,x+width-1*MenuScale,y+height-1*MenuScale
	Else
		Rect x,y,width,height
		Color 133,130,125
		Rect x,y,width-1*MenuScale,height-1*MenuScale,False	
		Color 250,250,250
		Rect x,y,width,height,False
		Color 10,10,10
		Line x,y+height-1,x+width-1,y+height-1
		Line x+width-1,y,x+width-1,y+height-1		
	EndIf
	
	Color 255,255,255
	If disabled Then Color 70,70,70
	Text x+width/2, y+height/2-1*MenuScale, txt, True, True
	
	Color 0,0,0
	
	If Pushed And MouseHit1 Then PlaySound_Strict ButtonSFX : Return True
End Function







;~IDEal Editor Parameters:
;~C#Blitz3D