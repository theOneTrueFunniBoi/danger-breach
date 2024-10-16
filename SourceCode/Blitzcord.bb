Global BlitzcordGameStatus=0

Global TimestampStart=MilliSecs()*1000

; stores discord rich presence bot id
Include "SourceCode\BlitzcordCode.bb"

BlitzcordCreateCore(BlitzcordCoreCode)

Function UpdateBlitzcord(gameStatusOverride=0)
	Local gameStatus=0
	
	If (gameStatusOverride) Then 
		gameStatus=gameStatusOverride
	Else
		gameStatus=BlitzcordGameStatus
	EndIf
	
	If (Not MemeMode)
		If (gameStatus=0) Then
			
			BlitzcordSetLargeImage( "temp_icn" )
			BlitzcordSetSmallImage( "" )
			BlitzcordSetActivityDetails( "ass" )
			BlitzcordSetActivityState( "balls" )
			;BlitzcordSetTimestampStart( TimestampStart )
			
		ElseIf (gameStatus=1)
			
			BlitzcordSetLargeImage( "icon" )
			BlitzcordSetSmallImage( "" )
			BlitzcordSetActivityDetails( "" )
			BlitzcordSetActivityState( "Main Menu" )
			;BlitzcordSetTimestampStart( TimestampStart )
			
		ElseIf (gameStatus=2)
			
			BlitzcordSetLargeImage( "icon" )
			BlitzcordSetSmallImage( "" )
			
			If SelectedMap = "" Then
				BlitzcordSetActivityDetails( "Seed: "+RandomSeed)
			Else
				BlitzcordSetActivityDetails( "Map:"+SelectedMap)
			EndIf
			
			BlitzcordSetActivityState( "In Game" )
			;BlitzcordSetTimestampStart( TimestampStart )
			
		ElseIf (gameStatus=3)
			
			BlitzcordSetLargeImage( "load_icn" )
			BlitzcordSetSmallImage( "" )
			BlitzcordSetActivityDetails( "Loading..." )
			BlitzcordSetActivityState( "" )
			;BlitzcordSetTimestampStart( TimestampStart )
			
		ElseIf (gameStatus=4)
			
			BlitzcordSetLargeImage( "icon" )
			BlitzcordSetSmallImage( "" )
			BlitzcordSetActivityDetails( "Launcher" )
			BlitzcordSetActivityState( "" )
			;BlitzcordSetTimestampStart( TimestampStart )
			
		ElseIf (gameStatus=5)
			
			BlitzcordSetLargeImage( "icon" )
			BlitzcordSetSmallImage( "" )
			BlitzcordSetActivityDetails( "Logos" )
			BlitzcordSetActivityState( "" )
			;BlitzcordSetTimestampStart( TimestampStart )
			
		ElseIf (gameStatus=6)
			
			BlitzcordSetLargeImage( "icon" )
			BlitzcordSetSmallImage( "" )
			BlitzcordSetActivityDetails( "Gate-A" )
			BlitzcordSetActivityState( "In Game" )
			;BlitzcordSetTimestampStart( TimestampStart )
			
		ElseIf (gameStatus=7)
			
			BlitzcordSetLargeImage( "icon" )
			BlitzcordSetSmallImage( "" )
			BlitzcordSetActivityDetails( "Gate-B" )
			BlitzcordSetActivityState( "In Game" )
			;BlitzcordSetTimestampStart( TimestampStart )
			
		EndIf
	Else
		If (gameStatus=0) Then
			
			BlitzcordSetLargeImage( "temp_icn" )
			BlitzcordSetSmallImage( "" )
			BlitzcordSetActivityDetails( "meme" )
			BlitzcordSetActivityState( "mode" )
			;BlitzcordSetTimestampStart( TimestampStart )
			
		ElseIf (gameStatus=1)
			
			BlitzcordSetLargeImage( "meme_icn" )
			BlitzcordSetSmallImage( "" )
			BlitzcordSetActivityDetails( "" )
			BlitzcordSetActivityState( "menu that is main" )
			;BlitzcordSetTimestampStart( TimestampStart )
			
		ElseIf (gameStatus=2)
			
			BlitzcordSetLargeImage( "meme_icn" )
			BlitzcordSetSmallImage( "" )
			
			If SelectedMap = "" Then
				BlitzcordSetActivityDetails( "(maybe) rando seed: "+RandomSeed)
			Else
				BlitzcordSetActivityDetails( "eww custom map:"+SelectedMap)
			EndIf
			
			BlitzcordSetActivityState( "playin" )
			;BlitzcordSetTimestampStart( TimestampStart )
			
		ElseIf (gameStatus=3)
			
			BlitzcordSetLargeImage( "meme_icn" )
			BlitzcordSetSmallImage( "" )
			BlitzcordSetActivityDetails( "loadin crap" )
			BlitzcordSetActivityState( "" )
			;BlitzcordSetTimestampStart( TimestampStart )
			
		ElseIf (gameStatus=4)
			
			BlitzcordSetLargeImage( "meme_icn" )
			BlitzcordSetSmallImage( "" )
			BlitzcordSetActivityDetails( "lauauauncher" )
			BlitzcordSetActivityState( "" )
			;BlitzcordSetTimestampStart( TimestampStart )
			
		ElseIf (gameStatus=5)
			
			BlitzcordSetLargeImage( "meme_icn" )
			BlitzcordSetSmallImage( "" )
			BlitzcordSetActivityDetails( "loogoes" )
			BlitzcordSetActivityState( "" )
			;BlitzcordSetTimestampStart( TimestampStart )
			
		ElseIf (gameStatus=6)
			
			BlitzcordSetLargeImage( "meme_icn" )
			BlitzcordSetSmallImage( "" )
			BlitzcordSetActivityDetails( "the 'good' ending" )
			BlitzcordSetActivityState( "playin" )
			;BlitzcordSetTimestampStart( TimestampStart )
			
		ElseIf (gameStatus=7)
			
			BlitzcordSetLargeImage( "meme_icn" )
			BlitzcordSetSmallImage( "" )
			BlitzcordSetActivityDetails( "something something 682" )
			BlitzcordSetActivityState( "playin" )
			;BlitzcordSetTimestampStart( TimestampStart )
			
		EndIf
	EndIf
	
	BlitzcordUpdateActivity()
	BlitzcordRunCallbacks()
	
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D