Global BlitzcordGameStatus=0

Global TimestampStart=0

BlitzcordCreateCore("1141189053409132585")

Function UpdateBlitzcord(gameStatusOverride=0)
	Local gameStatus=0
	
	If (gameStatusOverride) Then 
		gameStatus=gameStatusOverride
	Else
		gameStatus=BlitzcordGameStatus
	EndIf
	
	If (gameStatus=0) Then
		
		BlitzcordSetLargeImage( "temp_icn" )
		
		BlitzcordSetSmallImage( "" )
		
		BlitzcordSetActivityDetails( "test" )
		
		BlitzcordSetActivityState( "test" )
		;If (Not TimestampStart)
			;BlitzcordSetTimestampStart( MilliSecs() )
		;EndIf
		
		TimestampStart=1
		
	ElseIf (gameStatus=1)
		
		BlitzcordSetLargeImage( "icon" )
		
		BlitzcordSetSmallImage( "" )
		
		BlitzcordSetActivityDetails( "" )
		
		BlitzcordSetActivityState( "Main Menu" )
		
		;If (Not TimestampStart)
			;BlitzcordSetTimestampStart( MilliSecs() )
		;EndIf
		
		TimestampStart=1
		
	ElseIf (gameStatus=2)
		
		BlitzcordSetLargeImage( "icon" )
		
		BlitzcordSetSmallImage( "" )
		
		If SelectedMap = "" Then
				BlitzcordSetActivityDetails( "Seed: "+RandomSeed)
		Else
			BlitzcordSetActivityDetails( "Map:"+SelectedMap)
		EndIf
		
		BlitzcordSetActivityState( "In Game" )
		
		;If (Not TimestampStart)
			;BlitzcordSetTimestampStart( MilliSecs() )
		;EndIf
		
		TimestampStart=1
		
	ElseIf (gameStatus=3)
		
		BlitzcordSetLargeImage( "icon" )
		
		BlitzcordSetSmallImage( "" )
		
		BlitzcordSetActivityDetails( "Loading..." )
		
		BlitzcordSetActivityState( "" )
		
		;If (Not TimestampStart)
			;BlitzcordSetTimestampStart( MilliSecs() )
		;EndIf
		
		TimestampStart=1
		
	ElseIf (gameStatus=4)
		
		BlitzcordSetLargeImage( "icon" )
		
		BlitzcordSetSmallImage( "" )
		
		BlitzcordSetActivityDetails( "Launcher" )
		
		BlitzcordSetActivityState( "In Game" )
		
		;If (Not TimestampStart)
			;BlitzcordSetTimestampStart( MilliSecs() )
		;EndIf
		
		TimestampStart=1
		
	ElseIf (gameStatus=5)
		
		BlitzcordSetLargeImage( "icon" )
		
		BlitzcordSetSmallImage( "" )
		
		BlitzcordSetActivityDetails( "Logos" )
		
		BlitzcordSetActivityState( "In Game" )
		
		;If (Not TimestampStart)
			;BlitzcordSetTimestampStart( MilliSecs() )
		;EndIf
		
		TimestampStart=1
		
	ElseIf (gameStatus=6)
		
		BlitzcordSetLargeImage( "icon" )
		
		BlitzcordSetSmallImage( "" )
		
		BlitzcordSetActivityDetails( "Gate-A" )
		
		BlitzcordSetActivityState( "In Game" )
		
		;If (Not TimestampStart)
			;BlitzcordSetTimestampStart( MilliSecs() )
		;EndIf
		
		TimestampStart=1
		
	ElseIf (gameStatus=7)
		
		BlitzcordSetLargeImage( "icon" )
		
		BlitzcordSetSmallImage( "" )
		
		BlitzcordSetActivityDetails( "Gate-B" )
		
		BlitzcordSetActivityState( "In Game" )
		
		;If (Not TimestampStart)
			;BlitzcordSetTimestampStart( MilliSecs() )
		;EndIf
		
		TimestampStart=1
		
	EndIf
	
	BlitzcordUpdateActivity()
	
	BlitzcordRunCallbacks()
	
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D