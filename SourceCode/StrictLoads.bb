; ID: 2975
; Author: RifRaf, further modified by MonocleBios
; Date: 2012-09-11 11:44:22
; Title: Safe Loads (b3d) ;strict loads sounds more appropriate IMO
; Description: Get the missing filename reported

;safe loads for mav trapping media issues




;basic wrapper functions that check to make sure that the file exists before attempting to load it, raises an RTE if it doesn't
;more informative alternative to MAVs outside of debug mode, makes it immiediately obvious whether or not someone is loading resources
;likely to cause more crashes than 'clean' CB, as this prevents anyone from loading any assets that don't exist, regardless if they are ever used
;added zero checks since blitz load functions return zero sometimes even if the filetype exists
Function LoadImage_Strict(file$)
	;LoadingWhatAsset = file
	;If FileType(file$)<>1 Then RuntimeError "Image " + Chr(34) + file$ + Chr(34) + " missing. "
	tmp = LoadImage(file$)
	If tmp = 0 Then
		;attempt to load again
		DebugLog "Attempting to load again: "+file
		tmp2 = LoadImage(file)
		Return tmp2
	Else
		Return tmp
	EndIf
End Function

Function LoadAnimImage_Strict(file$,fCellwidth,fCellheight,fFirst,fCount)
	;LoadingWhatAsset = file
	;If FileType(file$)<>1 Then RuntimeError "Image " + Chr(34) + file$ + Chr(34) + " missing. "
	tmp = LoadAnimImage(file$,fCellwidth,fCellheight,fFirst,fCount)
	If tmp = 0 Then
		;attempt to load again
		DebugLog "Attempting to load again: "+file
		tmp2 = LoadAnimImage(file$,fCellwidth,fCellheight,fFirst,fCount)
		Return tmp2
	Else
		Return tmp
	EndIf
End Function


Function ExecFile_Strict(file$,newDir$="")
	If (Not newDir = "") Then ChangeDir(newDir)	
	If (FileType(file) = 0) Then RuntimeError("ILLEGAL APPLICATION '"+file+"', file does not exist.")
	If (FileSize(file) = 0) Then RuntimeError("ILLEGAL APPLICATION '"+file+"', file size is zero.")
	ExecFile(file)
End Function


Type Sound
	Field internalHandle%
	Field name$
	Field channels%[32]
	Field releaseTime%
End Type

Function AutoReleaseSounds()
	Local snd.Sound
	For snd.Sound = Each Sound
		Local tryRelease% = True
		For i = 0 To 31
			If snd\channels[i] <> 0 Then
				If ChannelPlaying(snd\channels[i]) Then
					tryRelease = False
					snd\releaseTime = MilliSecs2()+5000
					Exit
				EndIf
			EndIf
		Next
		If tryRelease Then
			If snd\releaseTime < MilliSecs2() Then
				If snd\internalHandle <> 0 Then
					FreeSound snd\internalHandle
					snd\internalHandle = 0
				EndIf
			EndIf
		EndIf
	Next
End Function

Function PlaySound_Strict%(sndHandle%)
	Local snd.Sound = Object.Sound(sndHandle)
	If snd <> Null Then
		Local shouldPlay% = True
		For i = 0 To 31
			If snd\channels[i] <> 0 Then
				If Not ChannelPlaying(snd\channels[i]) Then
					If snd\internalHandle = 0 Then
						If FileType(snd\name) <> 1 Then
							CreateConsoleMsg("Sound " + Chr(34) + snd\name + Chr(34) + " not found.")
							If ConsoleOpening
								ConsoleOpen = True
							EndIf
						Else
							If EnableSFXRelease Then snd\internalHandle = LoadSound(snd\name)
						EndIf
						If snd\internalHandle = 0 Then
							CreateConsoleMsg("Failed to load Sound: " + Chr(34) + snd\name + Chr(34))
							If ConsoleOpening
								ConsoleOpen = True
							EndIf
						EndIf
					EndIf
					If ConsoleFlush Then
						snd\channels[i] = PlaySound(ConsoleFlushSnd)
					Else
						snd\channels[i] = PlaySound(snd\internalHandle)
						LoadSubtitles(snd\name)
					EndIf
					ChannelVolume snd\channels[i],SFXVolume#
					snd\releaseTime = MilliSecs2()+5000 ;release after 5 seconds
					Return snd\channels[i]
				EndIf
			Else
				If snd\internalHandle = 0 Then
					If FileType(snd\name) <> 1 Then
						CreateConsoleMsg("Sound " + Chr(34) + snd\name + Chr(34) + " not found.")
						If ConsoleOpening
							ConsoleOpen = True
						EndIf
					Else
						If EnableSFXRelease Then snd\internalHandle = LoadSound(snd\name)
					EndIf
						
					If snd\internalHandle = 0 Then
						CreateConsoleMsg("Failed to load Sound: " + Chr(34) + snd\name + Chr(34))
						If ConsoleOpening
							ConsoleOpen = True
						EndIf
					EndIf
				EndIf
				If ConsoleFlushSnd Then
					snd\channels[i] = PlaySound(ConsoleFlushSnd)
				Else
					snd\channels[i] = PlaySound(snd\internalHandle)
					LoadSubtitles(snd\name)
				EndIf
				ChannelVolume snd\channels[i],SFXVolume#
				snd\releaseTime = MilliSecs2()+5000 ;release after 5 seconds
				Return snd\channels[i]
			EndIf
		Next
	EndIf
	
	Return 0
End Function

Function LoadSound_Strict(file$)
	;LoadingWhatAsset = file$
	Local snd.Sound = New Sound
	snd\name = file
	snd\internalHandle = 0
	snd\releaseTime = 0
	If (Not EnableSFXRelease) Then
		If snd\internalHandle = 0 Then 
			snd\internalHandle = LoadSound(snd\name)
		EndIf
	EndIf
	Return Handle(snd)
End Function

Function FreeSound_Strict(sndHandle%)
	Local snd.Sound = Object.Sound(sndHandle)
	If snd <> Null Then
		If snd\internalHandle <> 0 Then
			FreeSound snd\internalHandle
			snd\internalHandle = 0
		EndIf
		Delete snd
	EndIf
End Function

Type Stream
	Field sfx%
	Field chn%
End Type

Function StreamSound_Strict(file$,volume#=1.0,custommode=Mode)
	If FileType(file$)<>1
		CreateConsoleMsg("Sound " + Chr(34) + file$ + Chr(34) + " not found.")
		If ConsoleOpening
			ConsoleOpen = True
		EndIf
		Return 0
	EndIf
	
	Local st.Stream = New Stream
	st\sfx = FSOUND_Stream_Open(file$,custommode,0)
	;st\sfx = StreamSound(file$)
	If st\sfx = 0
		CreateConsoleMsg("Failed to stream Sound (returned 0): " + Chr(34) + file$ + Chr(34))
		If ConsoleOpening
			ConsoleOpen = True
		EndIf
		Return 0
	EndIf
	
	st\chn = FSOUND_Stream_Play(FreeChannel,st\sfx)
	;st\chn = PlaySound(st\sfx)
	If st\chn = -1
		CreateConsoleMsg("Failed to stream Sound (returned -1): " + Chr(34) + file$ + Chr(34))
		If ConsoleOpening
			ConsoleOpen = True
		EndIf
		Return -1
	EndIf
	;ChannelVolume(st\chn,volume*255)
	FSOUND_SetVolume(st\chn,volume*255)
	FSOUND_SetPaused(st\chn,False)
	;ResumeChannel(st\chn)
	Return Handle(st)
End Function

Function StopStream_Strict(streamHandle%)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null
		CreateConsoleMsg("Failed to stop stream Sound: Unknown Stream")
		Return
	EndIf
	If st\chn=0 Or st\chn=-1
		CreateConsoleMsg("Failed to stop stream Sound: Return value "+st\chn)
		Return
	EndIf
	;StopChannel(st\chn)
	FSOUND_StopSound(st\chn)
	FSOUND_Stream_Stop(st\sfx)
	FSOUND_Stream_Close(st\sfx)
	Delete st
	
End Function

Function SetStreamVolume_Strict(streamHandle%,volume#)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null
		CreateConsoleMsg("Failed to set stream Sound volume: Unknown Stream")
		Return
	EndIf
	If st\chn=0 Or st\chn=-1
		CreateConsoleMsg("Failed to set stream Sound volume: Return value "+st\chn)
		Return
	EndIf
	;ChannelVolume(st\chn,volume*255.0)
	FSOUND_SetVolume(st\chn,volume*255.0)
	FSOUND_SetPaused(st\chn,False)
	;ResumeChannel(st\chn)
End Function

Function SetStreamPaused_Strict(streamHandle%,paused%)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null
		CreateConsoleMsg("Failed to pause/unpause stream Sound: Unknown Stream")
		Return
	EndIf
	If st\chn=0 Or st\chn=-1
		CreateConsoleMsg("Failed to pause/unpause stream Sound: Return value "+st\chn)
		Return
	EndIf
	;If paused=1 Then PauseChannel(st\chn)
	FSOUND_SetPaused(st\chn,paused)
	;If paused=0 Then ResumeChannel(st\chn)
End Function

Function IsStreamPlaying_Strict(streamHandle%)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null
		CreateConsoleMsg("Failed to find stream Sound: Unknown Stream")
		Return
	EndIf
	If st\chn=0 Or st\chn=-1
		CreateConsoleMsg("Failed to find stream Sound: Return value "+st\chn)
		Return
	EndIf
	
	Return FSOUND_IsPlaying(st\chn)
	;Return ChannelPlaying(st\chn)
End Function

Function SetStreamPan_Strict(streamHandle%,pan#)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null
		CreateConsoleMsg("Failed to find stream Sound: Unknown Stream")
		Return
	EndIf
	If st\chn=0 Or st\chn=-1
		CreateConsoleMsg("Failed to find stream Sound: Return value "+st\chn)
		Return
	EndIf
	
	;-1 = Left = 0
	;0 = Middle = 127.5 (127)
	;1 = Right = 255
	Local fmod_pan% = 0
	fmod_pan% = Int((255.0/2.0)+((255.0/2.0)*pan#))
	FSOUND_SetPan(st\chn,fmod_pan%)
	;ChannelPan(st\chn,pan)
End Function

Function UpdateStreamSoundOrigin(streamHandle%,cam%,entity%,range#=10,volume#=1.0)
	;Local st.Stream = Object.Stream(streamHandle)
	range# = Max(range,1.0)
	
	If volume>0 Then
		
		Local dist# = EntityDistance(cam, entity) / range#
		If 1 - dist# > 0 And 1 - dist# < 1 Then
			
			Local panvalue# = Sin(-DeltaYaw(cam,entity))
			
			SetStreamVolume_Strict(streamHandle,volume#*(1-dist#)*SFXVolume#)
			SetStreamPan_Strict(streamHandle,panvalue)
		Else
			SetStreamVolume_Strict(streamHandle,0.0)
		EndIf
	Else
		If streamHandle <> 0 Then
			SetStreamVolume_Strict(streamHandle,0.0)
		EndIf 
	EndIf
End Function

Function LoadMesh_Strict(File$,parent=0)
	CatchErrors("(Uncaught) LoadMesh_Strict - "+File)
	LoadingWhatAsset = File$
	UpdateLoading() ;think of better way later
	If FileType(File$) <> 1 Then RuntimeError "3D Mesh " + File$ + " not found."
	Local tmp = LoadMesh(File$, parent)
	;If tmp = 0 Then RuntimeError "Failed to load 3D Mesh: " + File$
	If tmp = 0 Then
		;attempt to load again
		DebugLog "Attempting to load again: "+File
		Local tmp2 = LoadMesh(File$,parent)
		If tmp2 = 0 Then RuntimeError "Failed to load 3D Mesh: " + File$ 
		Return tmp2
	Else
		Return tmp
	EndIf
	CatchErrors("LoadMesh_Strict - "+File)
End Function   

Function LoadAnimMesh_Strict(File$,parent=0)
	CatchErrors("(Uncaught) LoadAnimMesh_Strict - "+File)
	DebugLog File
	LoadingWhatAsset = File$
	UpdateLoading() ;think of better way later
	If FileType(File$) <> 1 Then RuntimeError "3D Animated Mesh " + File$ + " not found."
	Local tmp = LoadAnimMesh(File$, parent)
	;If tmp = 0 Then RuntimeError "Failed to load 3D Animated Mesh: " + File$ 
	If tmp = 0 Then
		;attempt to load again
		DebugLog "Attempting to load again: "+File
		Local tmp2 = LoadAnimMesh(File$,parent)
		If tmp2 = 0 Then RuntimeError "Failed to load 3D Animated Mesh: " + File$ 
		Return tmp2
	Else
		Return tmp
	EndIf
	CatchErrors("LoadAnimMesh_Strict - "+File)
End Function   

;don't use in LoadRMesh, as Reg does this manually there. If you wanna fuck around with the logic in that function, be my guest 
Function LoadTexture_Strict(File$,flags=1)
	;LoadingWhatAsset = File$
	;If FileType(File$) <> 1 Then RuntimeError "Texture " + File$ + " not found."
	tmp = LoadTexture(File$, flags+(256*(EnableVRam=True)))
	;If tmp = 0 Then RuntimeError "Failed to load Texture: " + File$ 
	If tmp = 0 Then
		;attempt to load again
		DebugLog "Attempting to load again: "+File
		tmp2 = LoadTexture(File$, flags+(256*(EnableVRam=True)))
		Return tmp2
	Else
		Return tmp
	EndIf 
End Function   

Function LoadBrush_Strict(file$,flags,u#=1.0,v#=1.0)
	LoadingWhatAsset = file$
	UpdateLoading() ;think of better way later
	;If FileType(file$)<>1 Then RuntimeError "Brush Texture " + file$ + "not found."
	tmp = LoadBrush(file$, flags, u, v)
	;If tmp = 0 Then RuntimeError "Failed to load Brush: " + file$ 
	If tmp = 0 Then
		;attempt to load again
		DebugLog "Attempting to load again: "+file
		tmp2 = LoadBrush(file$, flags, u, v)
		Return tmp2
	Else
		Return tmp
	EndIf 
End Function 

Function LoadFont_Strict(file$="Tahoma", height=13, bold=0, italic=0, underline=0)
	LoadingWhatAsset = file$
	UpdateLoading() ;think of better way later
	;If FileType(file$)<>1 Then RuntimeError "Font " + file$ + " not found."
	tmp = LoadFont(file, height, bold, italic, underline)  
	;If tmp = 0 Then RuntimeError "Failed to load Font: " + file$ 
	If tmp = 0 Then
		;attempt to load again
		DebugLog "Attempting to load again: "+file
		tmp2 = LoadFont(file, height, bold, italic, underline)  
		Return tmp2
	Else
		Return tmp
	EndIf 
End Function





;~IDEal Editor Parameters:
;~C#Blitz3D