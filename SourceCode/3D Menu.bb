;3D Main Menu for SCP - Containment Breach

Global MenuMesh%,MenuCam%,MenuRoom$ ;room and camera
Global Menu3DScale#
Global MenuMisc%[10] ;NPCs and other stuff
Global MenuSprite%[4],MenuDark# = 1.0
Global MenuState#,MenuState2#
Global MenuSoundChn%=0,MenuSoundChn2%=0,MenuSoundChn3%

Global MenuLights%[10],MenuLightSprites%[10]

Global In3dMenu%=0

Global SoundLoc%

Global MenuSky

Function Init3DMenu()

	LoadingWhatAsset = "3D Main Menu"

	Select Rand(0, 7)
		Case 0
			MenuMesh = LoadRMesh_3DMenu("GFX\map\173new2dark_opt.rmesh")
			MenuRoom = "start"
			
		Case 1
			MenuMesh = LoadRMesh_3DMenu("GFX\map\room009.rmesh")
			MenuRoom = "009"
			
		Case 2
			MenuMesh = LoadRMesh_3DMenu("GFX\map\checkpoint1_opt.rmesh")
			MenuRoom = "checkpoint1"
			
		Case 3
			MenuMesh = LoadRMesh_3DMenu("GFX\map\room079_elevator.rmesh")
			MenuRoom = "079"
			
		Case 4
			MenuMesh = LoadRMesh_3DMenu("GFX\map\machineroom_opt_large.rmesh")
			MenuRoom = "914"
			
		Case 5
			MenuMesh = LoadRMesh_3DMenu("GFX\map\gateaentrance_opt.rmesh")
			MenuRoom = "gatea"
			
		Case 6
			MenuMesh = LoadRMesh_3DMenu("GFX\map\room018.rmesh")
			MenuRoom = "018"
			
		Case 7
			MenuMesh = LoadRMesh_3DMenu("GFX\map\gatea_new.rmesh")
			MenuRoom = "gateatop"
			
	End Select
	
	MenuSoundChn%=0
	
	MenuSoundChn2%=0
	
	MenuSoundChn3%=0
	
	MenuSky = 0
	
	;DrawLoading(100, True)
End Function

Function Init3dMenuQuick()
	In3dMenu=1
	
	ScaleEntity MenuMesh,RoomScale,RoomScale,RoomScale
	MenuCam = CreateCamera()
	CameraClsColor MenuCam,0,0,0
	CameraRange MenuCam,0.1,40.0
	CameraViewport MenuCam,0,0,GraphicWidth,GraphicHeight
	
	MenuSprite[0] = CreateSprite(MenuCam)
	EntityOrder MenuSprite[0],-100
	ScaleSprite MenuSprite[0],0.45,0.45/650.0*228.0
	Menu3DScale = Min(1.0,Float(GraphicHeight)/Float(GraphicWidth))
	MoveEntity MenuSprite[0],Menu3DScale*1.03,Menu3DScale*0.75,1.0
	Local temptex% = LoadTexture("GFX\menu\back3d.jpg",3)
	
	;MenuScale = Menu3DScale
	
	EntityTexture MenuSprite[0],temptex
	
	MenuSprite[1] = CopyEntity(MenuSprite[0])
	EntityParent MenuSprite[1],MenuCam
	EntityColor MenuSprite[1],0,0,0
	EntityAlpha MenuSprite[1],0.3
	
	EntityOrder MenuSprite[1],-99
	
	MenuSprite[2] = CreateSprite()
	
	ScaleSprite MenuSprite[2],1.0,1.0
	;ScaleSprite MenuSprite[2],ImageWidth(MenuSprite[2]) * MenuScale, ImageHeight(MenuSprite[2]) * MenuScale
	MoveEntity MenuSprite[2],0.0,0.0,1.0
	EntityOrder MenuSprite[2],-98
	EntityColor MenuSprite[2],0,0,0
	EntityParent MenuSprite[2],MenuCam
	
	;MenuSprite[3] = CreateSprite(MenuCam)
	;MoveEntity MenuSprite[3],0.0,0,0.0
	;ScaleSprite MenuSprite[3],0.4,0.4
	;EntityOrder MenuSprite[3],-97
	;EntityParent MenuSprite[3],MenuCam
	;temptex% = LoadTexture("GFX\menu\back3D2.jpg",3)
	
	;EntityTexture MenuSprite[3],temptex
	
	;DrawImage(MenuBack3D, 0, 0)
	
	MenuSprite[3] = CreateSprite()
	
	Local temptex2% = LoadTexture("GFX\menu\back3D2.png",2)
	
	EntityTexture MenuSprite[3],temptex2
	
	ScaleSprite MenuSprite[3],1.0,1.0
	;ScaleSprite MenuSprite[3],ImageWidth(MenuSprite[2]) * MenuScale, ImageHeight(MenuSprite[2]) * MenuScale
	MoveEntity MenuSprite[3],0.0,0.0,1.0
	EntityOrder MenuSprite[3],-97
	;EntityColor MenuSprite[3],0,0,0
	EntityParent MenuSprite[3],MenuCam
	
	;MenuSprite[4] = CopyEntity(MenuSprite[3])
	
	;temptex2% = LoadTexture("GFX\menu\menuREC.png",2)
	
	;EntityTexture MenuSprite[4],temptex2
	
	;ScaleSprite MenuSprite[4],1.0,1.0
	;ScaleSprite MenuSprite[4],ImageWidth(MenuSprite[2]) * MenuScale, ImageHeight(MenuSprite[2]) * MenuScale
	;MoveEntity MenuSprite[4],0.0,0.0,1.0
	;EntityOrder MenuSprite[4],-96
	;EntityColor MenuSprite[4],0,0,0
	;EntityParent MenuSprite[4],MenuCam
	
	;EntityAlpha MenuSprite[4],100
	
	FreeTexture temptex
	
	FreeTexture temptex2
	
	MenuSoundChn%=0
	
	MenuSoundChn2%=0
	
	MenuSoundChn3%=0
	
	Select MenuRoom
		Case "gatea"
			TranslateEntity MenuCam,-448 * RoomScale,703 * RoomScale,-1 * RoomScale
			PointEntity MenuCam,MenuMesh
			RotateEntity MenuCam,20,-30,0,True
			CameraFogRange MenuCam,0.1,6.0
			CameraFogMode MenuCam,1
		Case "gateatop"
			MenuSky = sky_CreateSky("GFX\map\sky\sky")
			;RotateEntity MenuSky,0,e\room\angle,0
			TranslateEntity MenuCam,-448 * RoomScale,652 * RoomScale,-1 * RoomScale
			PointEntity MenuCam,MenuMesh
			RotateEntity MenuCam,20,-30,0,True
			CameraFogRange MenuCam, 5,30			
			angle = Max(Sin(EntityYaw(MenuCam)+90),0.0)
			;240,220,200
			CameraFogColor (MenuCam,200+(angle*40),200+(angle*20),200)
			CameraClsColor (MenuCam,200+(angle*40),200+(angle*20),200)
			CameraRange(MenuCam, 0.05, 60)
			AmbientLight (140, 140, 140)
			CameraFogMode MenuCam,0
		Case "start"
			AmbientLight Brightness,Brightness,Brightness
			TranslateEntity MenuCam,-336.0 * RoomScale,352 * RoomScale,48.0 * RoomScale
			CameraFogRange MenuCam,0.1,6.0
			CameraFogMode MenuCam,1
			;PointEntity MenuCam,MenuMesh
			MenuMisc[0]=LoadMesh("GFX\map\doorframe.x")
			MenuMisc[1]=LoadMesh("GFX\map\door01.x")
			MenuMisc[2]=LoadMesh("GFX\map\Button.x")
			PositionEntity MenuMisc[0],0.0,0.0,-4.0,True
			PositionEntity MenuMisc[1],0.0,0.0,-4.0,True
			PositionEntity MenuMisc[2],-0.6,0.7,-3.9,True
			ScaleEntity MenuMisc[0],RoomScale,RoomScale,RoomScale
			ScaleEntity MenuMisc[1],55 * RoomScale,55 * RoomScale,55 * RoomScale
			ScaleEntity MenuMisc[2], 0.03, 0.03, 0.03
			RotateEntity MenuMisc[2],0,180,0,True
			EntityParent MenuMisc[0],MenuMesh
			EntityParent MenuMisc[1],MenuMesh
			EntityParent MenuMisc[2],MenuMesh
			
		Case "018"
			TranslateEntity MenuCam, -800 * RoomScale,288 * RoomScale,-340.0 * RoomScale
			PointEntity MenuCam,MenuMesh
			RotateEntity MenuCam,10,-30,0,True
			CameraFogRange MenuCam,0.1,6.0
			CameraFogMode MenuCam,1
		Case "009"
			AmbientLight Brightness,Brightness,Brightness
			TranslateEntity MenuCam,932 * RoomScale,-4549 * RoomScale,492 * RoomScale
			;TranslateEntity MenuCam,-3.0,2.5,-3.6
			;TranslateEntity MenuCam,0,0,0
			CameraFogRange MenuCam,0.1,6.0
			CameraFogMode MenuCam,1
			PointEntity MenuCam,MenuMesh
			
			MenuMisc[0]=LoadMesh("GFX\map\doorframe.x")
			MenuMisc[1]=LoadMesh("GFX\map\heavydoor1.x")
			MenuMisc[2]=LoadMesh("GFX\map\heavydoor2.x")
			PositionEntity MenuMisc[0],0.0,-4896 * RoomScale,-1024 * RoomScale,True
			PositionEntity MenuMisc[1],0.0,-4896 * RoomScale,-1024 * RoomScale,True
			PositionEntity MenuMisc[2],0.0,-4896 * RoomScale,-1024 * RoomScale,True
			ScaleEntity MenuMisc[0],RoomScale,RoomScale,RoomScale
			ScaleEntity MenuMisc[1],RoomScale,RoomScale,RoomScale
			ScaleEntity MenuMisc[2],RoomScale,RoomScale,RoomScale
			RotateEntity MenuMisc[2],0,180,0,True
			EntityParent MenuMisc[0],MenuMesh
			EntityParent MenuMisc[1],MenuMesh
			EntityParent MenuMisc[2],MenuMesh
			
			MenuMisc[3]=LoadMesh("GFX\map\doorframe.x")
			MenuMisc[4]=LoadMesh("GFX\map\heavydoor1.x")
			MenuMisc[5]=LoadMesh("GFX\map\heavydoor2.x")
			PositionEntity MenuMisc[3],800 * RoomScale,-4896 * RoomScale,-800 * RoomScale,True
			PositionEntity MenuMisc[4],800 * RoomScale,-4896 * RoomScale,-800 * RoomScale,True
			PositionEntity MenuMisc[5],800 * RoomScale,-4896 * RoomScale,-800 * RoomScale,True
			ScaleEntity MenuMisc[3],RoomScale,RoomScale,RoomScale
			ScaleEntity MenuMisc[4],RoomScale,RoomScale,RoomScale
			ScaleEntity MenuMisc[5],RoomScale,RoomScale,RoomScale
			RotateEntity MenuMisc[3],0,-45,0,True
			RotateEntity MenuMisc[4],0,-45,0,True
			RotateEntity MenuMisc[5],0,-225,0,True
			EntityParent MenuMisc[3],MenuMesh
			EntityParent MenuMisc[4],MenuMesh
			EntityParent MenuMisc[5],MenuMesh
			
			MenuSoundChn2%=PlaySound_Strict(ElevatorBeepSFX)
			
			MenuSoundChn3%=PlaySound_Strict(OpenDoorSFX(3,1))
			
		Case "checkpoint1"
			AmbientLight Brightness,Brightness,Brightness
			TranslateEntity MenuCam,255 * RoomScale,572 * RoomScale,511 * RoomScale
			CameraFogRange MenuCam,0.1,6.0
			CameraFogMode MenuCam,1
			PointEntity MenuCam,MenuMesh
			
			;MenuMisc[0]=LoadMesh("GFX\map\doorframe.x")
			;MenuMisc[1]=LoadMesh("GFX\map\door01.x")
			;PositionEntity MenuMisc[0],48.0*RoomScale,0.0,-128.0 * RoomScale,True
			;PositionEntity MenuMisc[1],48.0*RoomScale,0.0,-128.0 * RoomScale,True
			;ScaleEntity MenuMisc[0],RoomScale,RoomScale,RoomScale
			;ScaleEntity MenuMisc[1],55 * RoomScale,55 * RoomScale,55 * RoomScale
			;RotateEntity MenuMisc[1],0,180,0,True
			;EntityParent MenuMisc[0],MenuMesh
			;EntityParent MenuMisc[1],MenuMesh
			
			;MenuMisc[2]=LoadMesh("GFX\map\doorframe.x")
			;MenuMisc[3]=LoadMesh("GFX\map\door01.x")
			;PositionEntity MenuMisc[2],-352.0*RoomScale,0.0,-128.0 * RoomScale,True
			;PositionEntity MenuMisc[3],-352.0*RoomScale,0.0,-128.0 * RoomScale,True
			;ScaleEntity MenuMisc[2],RoomScale,RoomScale,RoomScale
			;ScaleEntity MenuMisc[3],55 * RoomScale,55 * RoomScale,55 * RoomScale
			;RotateEntity MenuMisc[3],0,180,0,True
			;EntityParent MenuMisc[2],MenuMesh
			;EntityParent MenuMisc[3],MenuMesh
			
		Case "079"
			AmbientLight Brightness,Brightness,Brightness
			TranslateEntity MenuCam,1216 * RoomScale,-148 * RoomScale,255 * RoomScale
			CameraFogRange MenuCam,0.1,6.0
			CameraFogMode MenuCam,1
			PointEntity MenuCam,MenuMesh
			
			RotateEntity MenuCam,25,-146,343,True
			
			MenuMisc[0]=LoadMesh("GFX\map\079.b3d")
			ScaleEntity(MenuMisc[0], 1.3, 1.3, 1.3, True)
			PositionEntity (MenuMisc[0], 1856.0*RoomScale, -560.0*RoomScale, -672.0*RoomScale, True)
			EntityParent(MenuMisc[0], MenuMesh)
			RotateEntity MenuMisc[0],0,180,0,True
			
		Case "914"
			AmbientLight Brightness,Brightness,Brightness
			TranslateEntity MenuCam,383 * RoomScale,337 * RoomScale,-895 * RoomScale
			CameraFogRange MenuCam,0.1,6.0
			CameraFogMode MenuCam,1
			PointEntity MenuCam,MenuMesh
			
			RotateEntity MenuCam,25,-146,343,True
			
			MenuMisc[0]=LoadMesh("GFX\map\doorframe.x")
			MenuMisc[1]=LoadMesh("GFX\map\door01.x")
			MenuMisc[2]=LoadMesh("GFX\map\Button.x")
			PositionEntity MenuMisc[0],0.0,0.0,-4.0,True
			PositionEntity MenuMisc[1],0.0,0.0,-4.0,True
			PositionEntity MenuMisc[2],-0.6,0.7,-3.9,True
			ScaleEntity MenuMisc[0],RoomScale,RoomScale,RoomScale
			ScaleEntity MenuMisc[1],55 * RoomScale,55 * RoomScale,55 * RoomScale
			ScaleEntity MenuMisc[2], 0.03, 0.03, 0.03
			RotateEntity MenuMisc[2],0,180,0,True
			EntityParent MenuMisc[0],MenuMesh
			EntityParent MenuMisc[1],MenuMesh
			EntityParent MenuMisc[2],MenuMesh
			
			MenuMisc[3]=LoadMesh("GFX\map\ContDoorLeft.x")
			ScaleEntity(MenuMisc[3], 55 * RoomScale,55 * RoomScale,55 * RoomScale, True)
			PositionEntity (MenuMisc[3], 0*RoomScale, 0*RoomScale, -368.0*RoomScale, True)
			EntityParent(MenuMisc[3], MenuMesh)
			RotateEntity MenuMisc[3],0,0,0,True
			
			MenuMisc[4]=LoadMesh("GFX\map\ContDoorRight.x")
			ScaleEntity(MenuMisc[4], 55 * RoomScale,55 * RoomScale,55 * RoomScale, True)
			PositionEntity (MenuMisc[4], 0*RoomScale, 0*RoomScale, -368.0*RoomScale, True)
			EntityParent(MenuMisc[4], MenuMesh)
			RotateEntity MenuMisc[4],0,0,0,True
			
	End Select
	
	ClearTextureCache
End Function

Function DeInit3DMenu()
	FreeEntity MenuSprite[0] : MenuSprite[0] = 0
	FreeEntity MenuSprite[1] : MenuSprite[1] = 0
	FreeEntity MenuSprite[2] : MenuSprite[2] = 0
	FreeEntity MenuSprite[3] : MenuSprite[3] = 0
	For i%=0 To 9
		If MenuMisc[i]<>0 Then FreeEntity MenuMisc[i] : MenuMisc[i]=0
	Next
	MenuRoom = ""
	MenuState = 0 : MenuState2 = 0
	If MenuSoundChn<>0 Then
		StopChannel MenuSoundChn
		MenuSoundChn = 0
	EndIf
	If MenuSoundChn2<>0 Then
		StopChannel MenuSoundChn2
		MenuSoundChn2 = 0
	EndIf
	If MenuSoundChn3<>0 Then
		StopChannel MenuSoundChn3
		MenuSoundChn3 = 0
	EndIf
	
	FreeEntity MenuCam : MenuCam = 0
		
	FreeEntity MenuMesh : MenuMesh = 0
	
	FreeEntity model : model = 0
	
	FreeEntity SoundLoc : SoundLoc = 0
	
	MenuDark = 1.0
	
	In3dMenu=0
End Function

Function Update3DMenu()

	;EntityTexture MenuSprite[0],temptex

	If Rand(10)=1 Then
		EntityAlpha MenuSprite[1],Rnd(0.5,0.8)
		PositionEntity MenuSprite[1],EntityX(MenuSprite[0],True),EntityY(MenuSprite[0],True),EntityZ(MenuSprite[0],True),True
		MoveEntity MenuSprite[1],Rnd(-0.1,0.1)*Menu3DScale,Rnd(-0.1,0.1)*Menu3DScale,0.0
	Else
		EntityAlpha MenuSprite[1],0
	EndIf
	
	If MainMenuTab=0 Then
		MenuDark=Max(MenuDark-FPSfactor*0.05,0.0)
		EntityAlpha MenuSprite[0],1.0-MenuDark
	;ElseIf MainMenuTab=5 Then
	;	MenuDark=Min(MenuDark+FPSfactor*0.05,1.0)
	;	EntityAlpha MenuSprite[0],1.0-MenuDark
	Else
		MenuDark=Min(MenuDark+FPSfactor*0.05,0.7)
		EntityAlpha MenuSprite[0],1.0-MenuDark
	EndIf
	EntityAlpha MenuSprite[2],MenuDark
	
	Select MenuRoom
	
		Case "gatea"
			
			RotateEntity MenuCam,10,-30,0,True
			
			RotateEntity MenuCam,10,EntityYaw(MenuCam,True)+(Sin(MenuState)*15.0),0,True
			
			MenuState=WrapAngle(MenuState+FPSfactor*0.3)
			
			MenuState2=MenuState2+FPSfactor
			
			SoundLoc=CreatePivot()
			PositionEntity(SoundLoc,0.0,0.0,448*RoomScale,True)
			
			If MenuSoundChn = 0 Then
				MenuSoundChn = LoopSound2(AlarmSFX(0), MenuSoundChn, MenuCam, SoundLoc, 5.0)
			Else
				If Not ChannelPlaying(MenuSoundChn) Then MenuSoundChn = LoopSound2(AlarmSFX(0), MenuSoundChn, MenuCam, SoundLoc, 5.0)
			End If
			
		Case "gateatop"
			
			RotateEntity MenuCam,10,-30,0,True
			
			RotateEntity MenuCam,10,EntityYaw(MenuCam,True)+(Sin(MenuState)*15.0),0,True
			
			MenuState=WrapAngle(MenuState+FPSfactor*0.3)
			
			MenuState2=MenuState2+FPSfactor
			
			UpdateSky(MenuSky,MenuCam)
			
		Case "018"
		
			RotateEntity MenuCam,20,-30,0,True
			
			RotateEntity MenuCam,20,EntityYaw(MenuCam,True)+(Sin(MenuState)*15.0),0,True
			
			MenuState=WrapAngle(MenuState+FPSfactor*0.3)
			
			MenuState2=MenuState2+FPSfactor
			
			SoundLoc=CreatePivot()
			PositionEntity(SoundLoc,0.0,0.0,0,True)
			
			If MenuSoundChn = 0 Then
				MenuSoundChn = LoopSound2(AlarmSFX(3), MenuSoundChn, MenuCam, MenuCam, 2.0, 0.4)
			Else
				If Not ChannelPlaying(MenuSoundChn) Then MenuSoundChn = LoopSound2(AlarmSFX(3), MenuSoundChn, MenuCam, MenuCam, 2.0, 0.4)
			End If
	
		Case "start"
			PointEntity MenuCam,MenuMesh
			
			RotateEntity MenuCam,30,EntityYaw(MenuCam,True)+(Sin(MenuState)*15.0),0,True
			
			MenuState=WrapAngle(MenuState+FPSfactor*0.3)
			
			MenuState2=MenuState2+FPSfactor
			
			If MenuSoundChn = 0 Then
				MenuSoundChn = LoopSound2(AlarmSFX(0), MenuSoundChn, MenuCam, MenuCam, 2.0, 0.4)
			Else
				If Not ChannelPlaying(MenuSoundChn) Then MenuSoundChn = LoopSound2(AlarmSFX(0), MenuSoundChn, MenuCam, MenuCam, 2.0, 0.4)
			End If
			
		Case "009"
		
			;TranslateEntity MenuCam,932,-4549,492
			
			PointEntity MenuCam,MenuMesh
			
			RotateEntity MenuCam,18,EntityYaw(MenuCam,True)+(Sin(MenuState)*15.0),0,True
			
			MenuState=WrapAngle(MenuState+FPSfactor*0.3)
			
			MenuState2=MenuState2+FPSfactor
			
			If MenuSoundChn = 0 Then
				MenuSoundChn = LoopSound2(AlarmSFX(3), MenuSoundChn, MenuCam, MenuCam, 2.0, 0.4)
			Else
				If Not ChannelPlaying(MenuSoundChn) Then MenuSoundChn = LoopSound2(AlarmSFX(3), MenuSoundChn, MenuCam, MenuCam, 2.0, 0.4)
			End If
			
		Case "checkpoint1"
		
			;TranslateEntity MenuCam,932,-4549,492
			
			PointEntity MenuCam,MenuMesh
			
			RotateEntity MenuCam,20,EntityYaw(MenuCam,True)+(Sin(MenuState)*15.0),0,True
			
			MenuState=WrapAngle(MenuState+FPSfactor*0.3)
			
			MenuState2=MenuState2+FPSfactor
			
		Case "079"
		
			;PointEntity MenuCam,MenuMesh
			
			RotateEntity MenuCam,25,-146,343,True
		
			RotateEntity MenuCam,25,EntityYaw(MenuCam,True)+(Sin(MenuState)*15.0),0,True
			
			MenuState=WrapAngle(MenuState+FPSfactor*0.3)
			
			MenuState2=MenuState2+FPSfactor
			
		Case "914"
		
			RotateEntity MenuCam,10,44,10,True
		
			RotateEntity MenuCam,10,EntityYaw(MenuCam,True)+(Sin(MenuState)*15.0),0,True
			
			MenuState=WrapAngle(MenuState+FPSfactor*0.3)
			
			MenuState2=MenuState2+FPSfactor
			
	End Select
	
	RenderWorld()
End Function

Function LoadRMesh_3DMenu(file$) ;this ignores some stuff that we don't need
	
	;generate a texture made of white
	Local blankTexture%
	blankTexture=CreateTexture(4,4,1,1)
	ClsColor 255,255,255
	SetBuffer TextureBuffer(blankTexture)
	Cls
	SetBuffer BackBuffer()
	ClsColor 0,0,0
	
	;read the file
	Local f%=ReadFile(file)
	Local i%,j%,k%,x#,y#,z#,yaw#
	Local vertex%
	Local temp1i%,temp2i%,temp3i%
	Local temp1#,temp2#,temp3#
	Local temp1s$,temp2s$
	For i=0 To 3 ;reattempt up to 3 times
		If f=0 Then
			f=ReadFile(file)
		Else
			Exit
		EndIf
	Next
	
	;RuntimeError ReadString(f)
	
	Local Temp4$=""
	
	If f=0 Then RuntimeError "Error reading file "+Chr(34)+file+Chr(34)
	
	Temp4$=ReadString(f)
	
	DebugLog Temp4$
	
	If Temp4$<>"RoomMesh" Then
		If (Left(Temp4$,22)<>"RoomMesh.HasTriggerBox") Then
	
			RuntimeError Chr(34)+file+Chr(34)+" is not RMESH"
			
		EndIf
	EndIf
	
	file=StripFilename(file)
	
	Local count%,count2%
	;drawn meshes
	
	Local Opaque%,Alpha%
	
	Opaque=CreateMesh()
	Alpha=CreateMesh()
	
	count = ReadInt(f)
	Local childMesh%
	Local surf%,tex%[2],brush%
	
	Local isAlpha%
	
	Local u#,v#
	
	For i=1 To count ;drawn mesh
		childMesh=CreateMesh()
		
		surf=CreateSurface(childMesh)
		
		brush=CreateBrush()
		
		tex[0]=0 : tex[1]=0
		
		isAlpha=0
		For j=0 To 1
			temp1i=ReadByte(f)
			If temp1i<>0 Then
			
				temp1s=ReadString(f)
				;temp1s=ReadLine(f)
				tex[j]=GetTextureFromCache(temp1s)
				If tex[j]=0 Then ;texture is not in cache
					Select True
						Case temp1i<3
							tex[j]=LoadTexture(file+temp1s,1)
						Default
							tex[j]=LoadTexture(file+temp1s,3)
					End Select
					
					If tex[j]<>0 Then
						If temp1i=1 Then TextureBlend tex[j],5
						AddTextureToCache(tex[j])
					EndIf
					
				EndIf
				If tex[j]<>0 Then
					isAlpha=2
					If temp1i=3 Then isAlpha=1
					
					TextureCoords tex[j],1-j
				EndIf
			EndIf
		Next
		
		If isAlpha=1 Then
			If tex[1]<>0 Then
				TextureBlend tex[1],2
				BrushTexture brush,tex[1],0,0
			Else
				BrushTexture brush,blankTexture,0,0
			EndIf
		Else
			
;			If BumpEnabled And temp1s<>"" Then
;				bumptex = GetBumpFromCache(temp1s)	
;			Else
;				bumptex = 0
;			EndIf
			
;			If bumptex<>0 Then 
;			BrushTexture brush, tex[1], 0, 0	
;			BrushTexture brush, bumptex, 0, 1
;			If tex[0]<>0 Then 
;				BrushTexture brush, tex[0], 0, 2	
;			Else
;				BrushTexture brush,blankTexture,0,2
;			EndIf
;			Else
			For j=0 To 1
				If tex[j]<>0 Then
					BrushTexture brush,tex[j],0,j
				Else
					BrushTexture brush,blankTexture,0,j
				EndIf
			Next				
;			EndIf
			
		EndIf
		
		surf=CreateSurface(childMesh)
		
		If isAlpha>0 Then PaintSurface surf,brush
		
		FreeBrush brush : brush = 0
		
		count2=ReadInt(f) ;vertices
		
		For j%=1 To count2
			;world coords
			x=ReadFloat(f) : y=ReadFloat(f) : z=ReadFloat(f)
			vertex=AddVertex(surf,x,y,z)
			
			;texture coords
			For k%=0 To 1
				u=ReadFloat(f) : v=ReadFloat(f)
				VertexTexCoords surf,vertex,u,v,0.0,k
			Next
			
			;colors
			temp1i=ReadByte(f)
			temp2i=ReadByte(f)
			temp3i=ReadByte(f)
			VertexColor surf,vertex,temp1i,temp2i,temp3i,1.0
		Next
		
		count2=ReadInt(f) ;polys
		For j%=1 To count2
			temp1i = ReadInt(f) : temp2i = ReadInt(f) : temp3i = ReadInt(f)
			AddTriangle(surf,temp1i,temp2i,temp3i)
		Next
		
		If isAlpha=1 Then
			AddMesh childMesh,Alpha
		Else
			AddMesh childMesh,Opaque
		EndIf
		EntityParent childMesh,Opaque
		EntityAlpha childMesh,0.0
		EntityType childMesh,HIT_MAP
		EntityPickMode childMesh,2
		
	Next
	
	If BumpEnabled Then; And 0 Then
;		For i = 1 To CountSurfaces(Opaque)
;			surf = GetSurface(Opaque,i)
;			brush = GetSurfaceBrush(surf)
;			tex[0] = GetBrushTexture(brush,1)
;			temp1s$ =  StripPath(TextureName(tex[0]))
;			DebugLog temp1s$
;			If temp1s$<>0 Then 
;				mat.Materials=GetCache(temp1s)
;				If mat<>Null Then
;					If mat\Bump<>0 Then
;						tex[1] = GetBrushTexture(brush,0)
;						
;						BrushTexture brush, tex[1], 0, 2	
;						BrushTexture brush, mat\Bump, 0, 1
;						BrushTexture brush, tex[0], 0, 0					
;						
;						PaintSurface surf,brush
;						
;						;If tex[1]<>0 Then DebugLog "lkmlkm" : FreeTexture tex[1] : tex[1]=0
;					EndIf
;				EndIf
;				
;				;If tex[0]<>0 Then DebugLog "sdfsf" : FreeTexture tex[0] : tex[0]=0
;			EndIf
;			If brush<>0 Then FreeBrush brush : brush=0
;		Next
;		
		For i = 1 To CountSurfaces(Opaque)
			sf = GetSurface(Opaque,i)
			b = GetSurfaceBrush( sf )
			If b<>0 Then
				t = GetBrushTexture(b, 1)
				If t<>0 Then
					texname$ =  StripPath(TextureName(t))
					
					For mat.Materials = Each Materials
						If texname = mat\name Then
							If mat\Bump <> 0 Then 
								t1 = GetBrushTexture(b,0)
								
								If t1<>0 Then
									BrushTexture b, t1, 0, 2 ;light map
									BrushTexture b, mat\Bump, 0, 1 ;bump
									BrushTexture b, t, 0, 0 ;diff
									
									PaintSurface sf,b
									
									FreeTexture t1
								EndIf
								
								;If t1<>0 Then FreeTexture t1
								;If t2 <> 0 Then FreeTexture t2						
							EndIf
							Exit
						EndIf 
					Next
					
					FreeTexture t
				EndIf
				FreeBrush b
			EndIf
			
		Next
	EndIf
	
;	Local hiddenMesh%
;	hiddenMesh=CreateMesh()
	
	count=ReadInt(f) ;invisible collision mesh
	For i%=1 To count
		;surf=CreateSurface(hiddenMesh)
		count2=ReadInt(f) ;vertices
		For j%=1 To count2
			;world coords
			x=ReadFloat(f) : y=ReadFloat(f) : z=ReadFloat(f)
			;vertex=AddVertex(surf,x,y,z)
		Next
		
		count2=ReadInt(f) ;polys
		For j%=1 To count2
			temp1i = ReadInt(f) : temp2i = ReadInt(f) : temp3i = ReadInt(f)
			;AddTriangle(surf,temp1i,temp2i,temp3i)
		Next
	Next
	
	count=ReadInt(f) ;point entities
	
	Local lightTex% = LoadTexture("GFX\light1.jpg", 1)
	
	For i%=1 To count
	
		;RuntimeError ReadString(f)
	
		temp1s=ReadString(f)
		
		DebugLog temp1s
		
		Select temp1s
			Case "screen"
				
				temp1=ReadFloat(f)*RoomScale
				temp2=ReadFloat(f)*RoomScale
				temp3=ReadFloat(f)*RoomScale
				
				temp2s$ =ReadString(f)
				
				;If temp1<>0 Or temp2<>0 Or temp3<>0 Then 
				;	Local ts.TempScreens = New TempScreens	
				;	ts\x = temp1
				;	ts\y = temp2
				;	ts\z = temp3
				;	ts\imgpath = temp2s
				;	ts\roomtemplate = rt
				;EndIf
				
			Case "waypoint"
				
				temp1=ReadFloat(f)*RoomScale
				temp2=ReadFloat(f)*RoomScale
				temp3=ReadFloat(f)*RoomScale
				
				;Local w.TempWayPoints = New TempWayPoints
				;w\roomtemplate = rt
				;w\x = temp1
				;w\y = temp2
				;w\z = temp3
				
			Case "light"
				
				temp1=ReadFloat(f)*RoomScale
				temp2=ReadFloat(f)*RoomScale
				temp3=ReadFloat(f)*RoomScale
				
				If temp1<>0 Or temp2<>0 Or temp3<>0 Then 
					range# = ReadFloat(f)/2000.0
					lcolor$=ReadString(f)
					intensity# = Min(ReadFloat(f)*0.8,1.0)
					r%=Int(Piece(lcolor,1," "))*intensity
					g%=Int(Piece(lcolor,2," "))*intensity
					b%=Int(Piece(lcolor,3," "))*intensity
					
					;AddTempLight(rt, temp1,temp2,temp3, 2, range, r,g,b)
				Else
					ReadFloat(f) : ReadString(f) : ReadFloat(f)
				EndIf
				
			Case "spotlight"
				
				temp1=ReadFloat(f)*RoomScale
				temp2=ReadFloat(f)*RoomScale
				temp3=ReadFloat(f)*RoomScale
				
				If temp1<>0 Or temp2<>0 Or temp3<>0 Then 
					range# = ReadFloat(f)/2000.0
					lcolor$=ReadString(f)
					intensity# = Min(ReadFloat(f)*0.8,1.0)
					r%=Int(Piece(lcolor,1," "))*intensity
					g%=Int(Piece(lcolor,2," "))*intensity
					b%=Int(Piece(lcolor,3," "))*intensity
					
					;Local lt.LightTemplates = AddTempLight(rt, temp1,temp2,temp3, 2, range, r,g,b)
					angles$=ReadString(f)
					pitch#=Piece(angles,1," ")
					yaw#=Piece(angles,2," ")
					;lt\pitch = pitch
					;lt\yaw = yaw
					
					;lt\innerconeangle = ReadInt(f)
					;lt\outerconeangle = ReadInt(f)
				Else
					ReadFloat(f) : ReadString(f) : ReadFloat(f) : ReadString(f) : ReadInt(f) : ReadInt(f)
				EndIf
				
			Case "soundemitter"
				
				temp1i=0
				
				;For j = 0 To MaxRoomEmitters-1
				;	If rt\TempSoundEmitter[j]=0 Then
				;		rt\TempSoundEmitterX[j]=ReadFloat(f)*RoomScale
				;		rt\TempSoundEmitterY[j]=ReadFloat(f)*RoomScale
				;		rt\TempSoundEmitterZ[j]=ReadFloat(f)*RoomScale
				;		rt\TempSoundEmitter[j]=ReadInt(f)
				;		
				;		rt\TempSoundEmitterRange[j]=ReadFloat(f)
				;		temp1i=1
				;		Exit
				;	EndIf
				;Next
				;
				;If temp1i=0 Then
					ReadFloat(f)
					ReadFloat(f)
					ReadFloat(f)
					ReadInt(f)
					ReadFloat(f)
				;EndIf
				
			Case "playerstart"
				
				temp1=ReadFloat(f) : temp2=ReadFloat(f) : temp3=ReadFloat(f)
				
				angles$=ReadString(f)
				pitch#=Piece(angles,1," ")
				yaw#=Piece(angles,2," ")
				roll#=Piece(angles,3," ")
				If cam Then
					PositionEntity cam,temp1,temp2,temp3
					RotateEntity cam,pitch,yaw,roll
				EndIf
			Case "model"
				file = ReadString(f)
				If file<>""
					Local model = LoadMesh("GFX\Map\Props\"+file);LoadMesh("GFX\Map\Props\"+file)
					
					temp1=ReadFloat(f) : temp2=ReadFloat(f) : temp3=ReadFloat(f)
					PositionEntity model,temp1,temp2,temp3
					
					temp1=ReadFloat(f) : temp2=ReadFloat(f) : temp3=ReadFloat(f)
					RotateEntity model,temp1,temp2,temp3
					
					temp1=ReadFloat(f) : temp2=ReadFloat(f) : temp3=ReadFloat(f)
					ScaleEntity model,temp1,temp2,temp3
					
					EntityParent model,Opaque
					EntityType model,HIT_MAP
					EntityPickMode model,2
				Else
					DebugLog "file = 0"
					temp1=ReadFloat(f) : temp2=ReadFloat(f) : temp3=ReadFloat(f)
					DebugLog temp1+", "+temp2+", "+temp3
					
					;Stop
				EndIf
		End Select
	Next
	
	FreeTexture lightTex
	
	Local obj%
	
	temp1i=CopyMesh(Alpha)
	FlipMesh temp1i
	AddMesh temp1i,Alpha
	FreeEntity temp1i
	
	If brush <> 0 Then FreeBrush brush
	
	AddMesh Alpha,Opaque
	FreeEntity Alpha
	
	EntityFX Opaque,3
	
	;EntityAlpha hiddenMesh,0.0
	EntityAlpha Opaque,1.0
	
	;EntityType Opaque,HIT_MAP
	;EntityType hiddenMesh,HIT_MAP
	FreeTexture blankTexture
	
;	obj=CreatePivot()
;	CreatePivot(obj) ;skip "meshes" object
;	EntityParent Opaque,obj
;	EntityParent hiddenMesh,obj
;	CreatePivot(Room) ;skip "pointentites" object
;	CreatePivot(Room) ;skip "solidentites" object
	
	Return Opaque
	
End Function

;eof

;--------------------------------------- math -------------------------------------------------------
;
;Function Distance#(x1#, y1#, x2#, y2#)
;	Local x# = x2 - x1, y# = y2 - y1
;	Return(Sqr(x*x + y*y))
;End Function
;
;Function CurveValue#(number#, old#, smooth#)
;	If FPSfactor = 0 Then Return old
;	If number < old Then
;		Return Max(old + (number - old) * (1.0 / smooth * FPSfactor), number)
;	Else
;		Return Min(old + (number - old) * (1.0 / smooth * FPSfactor), number)
;	EndIf
;End Function
;
;Function CurveAngle#(val#, old#, smooth#)
;	If FPSfactor = 0 Then Return old
;	
;	Local diff# = WrapAngle(val) - WrapAngle(old)
;	If diff > 180 Then diff = diff - 360
;	If diff < - 180 Then diff = diff + 360
;	Return WrapAngle(old + (diff) * (1.0 / smooth * FPSfactor))
;End Function
;
;Function WrapAngle#(angle#)
;	If angle = INFINITY Then Return 0.0
;	While angle < 0
;		angle = angle + 360
;	Wend 
;	While angle >= 360
;		angle = angle - 360
;	Wend
;	Return angle
;End Function
;
;Function GetAngle#(x1#, y1#, x2#, y2#)
;	Return ATan2( y2 - y1, x2 - x1 )
;End Function
;
;Function CircleToLineSegIsect% (cx#, cy#, r#, l1x#, l1y#, l2x#, l2y#)
;	
;	;Palauttaa:
;	;  True (1) kun:
;	;      Ympyr‰ [keskipiste = (cx, cy): s‰de = r]
;	;      leikkaa janan, joka kulkee pisteiden (l1x, l1y) & (l2x, l2y) kaitta
;	;  False (0) muulloin
;	
;	;Ympyr‰n keskipisteen ja (ainakin toisen) janan p‰‰tepisteen et‰isyys < r
;	;-> leikkaus
;	If Distance(cx, cy, l1x, l1y) <= r Then
;		Return True
;	EndIf
;	
;	If Distance(cx, cy, l2x, l2y) <= r Then
;		Return True
;	EndIf	
;	
;	;Vektorit (janan vektori ja vektorit janan p‰‰tepisteist‰ ympyr‰n keskipisteeseen)
;	Local SegVecX# = l2x - l1x
;	Local SegVecY# = l2y - l1y
;	
;	Local PntVec1X# = cx - l1x
;	Local PntVec1Y# = cy - l1y
;	
;	Local PntVec2X# = cx - l2x
;	Local PntVec2Y# = cy - l2y
;	
;	;Em. vektorien pistetulot
;	Local dp1# = SegVecX * PntVec1X + SegVecY * PntVec1Y
;	Local dp2# = -SegVecX * PntVec2X - SegVecY * PntVec2Y
;	
;	;Tarkistaa onko toisen pistetulon arvo 0
;	;tai molempien merkki sama
;	If dp1 = 0 Or dp2 = 0 Then
;	ElseIf (dp1 > 0 And dp2 > 0) Or (dp1 < 0 And dp2 < 0) Then
;	Else
;		;Ei kumpikaan -> ei leikkausta
;		Return False
;	EndIf
;	
;	;Janan p‰‰tepisteiden kautta kulkevan suoran ;yht‰lˆ; (ax + by + c = 0)
;	Local a# = (l2y - l1y) / (l2x - l1x)
;	Local b# = -1
;	Local c# = -(l2y - l1y) / (l2x - l1x) * l1x + l1y
;	
;	;Ympyr‰n keskipisteen et‰isyys suorasta
;	Local d# = Abs(a * cx + b * cy + c) / Sqr(a * a + b * b)
;	
;	;Ympyr‰ on liian kaukana
;	;-> ei leikkausta
;	If d > r Then Return False
;	
;	;Local kateetin_pituus# = Cos(angle) * hyp
;	
;	;Jos p‰‰st‰‰n t‰nne saakka, ympyr‰ ja jana leikkaavat (tai ovat sis‰kk‰in)
;	Return True
;End Function
;
;Function Min#(a#, b#)
;	If a < b Then
;		Return a
;	Else
;		Return b
;	EndIf
;End Function
;
;Function Max#(a#, b#)
;	If a > b Then
;		Return a
;	Else
;		Return b
;	EndIf
;End Function
;
;
;;--------------------------------------- INI-functions -------------------------------------------------------
;
;Function GetINIString$(file$, section$, parameter$, defaultvalue$="")
;	Local TemporaryString$ = "", Temp%
;	Local f% = ReadFile(file)
;	
;	section = Lower(section)
;	
;	While Not Eof(f)
;		Local strtemp$ = ReadLine(f)
;		If Left(strtemp,1) = "[" Then
;			strtemp$ = Lower(strtemp)
;			If Mid(strtemp, 2, Len(strtemp)-2)=section Then
;				Repeat
;					TemporaryString = ReadLine(f)
;					If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(parameter) Then
;						CloseFile f
;						Return Trim( Right(TemporaryString,Len(TemporaryString)-Instr(TemporaryString,"=")) )
;					EndIf
;				Until Left(TemporaryString, 1) = "[" Or Eof(f)
;				
;				CloseFile f
;				Return defaultvalue
;			EndIf
;		EndIf
;	Wend
;	
;	Return defaultvalue
;	
;	CloseFile f
;End Function
;
;Function GetINIInt%(file$, section$, parameter$, defaultvalue% = 0)
;	Local txt$ = GetINIString(file$, section$, parameter$, defaultvalue)
;	If Lower(txt) = "true" Then
;		Return 1
;	ElseIf Lower(txt) = "false"
;		Return 0
;	Else
;		Return Int(txt)
;	EndIf
;End Function
;
;Function GetINIFloat#(file$, section$, parameter$, defaultvalue# = 0.0)
;	Return Float(GetINIString(file$, section$, parameter$, defaultvalue))
;End Function
;
;
;Function GetINIString2$(file$, start%, parameter$, defaultvalue$="")
;	Local TemporaryString$ = "", Temp%
;	Local f% = ReadFile(file)
;	
;	Local n%=0
;	While Not Eof(f)
;		Local strtemp$ = ReadLine(f)
;		n=n+1
;		If n=start Then 
;			Repeat
;				TemporaryString = ReadLine(f)
;				If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(parameter) Then
;					CloseFile f
;					Return Trim( Right(TemporaryString,Len(TemporaryString)-Instr(TemporaryString,"=")) )
;				EndIf
;			Until Left(TemporaryString, 1) = "[" Or Eof(f)
;			CloseFile f
;			Return defaultvalue
;		EndIf
;	Wend
;	
;	Return defaultvalue
;	
;	CloseFile f
;End Function
;
;Function GetINIInt2%(file$, start%, parameter$, defaultvalue$="")
;	Local txt$ = GetINIString2(file$, start%, parameter$, defaultvalue$)
;	If Lower(txt) = "true" Then
;		Return 1
;	ElseIf Lower(txt) = "false"
;		Return 0
;	Else
;		Return Int(txt)
;	EndIf
;End Function
;
;
;Function GetINISectionLocation%(file$, section$)
;	Local TemporaryString$ = "", Temp%
;	Local f% = ReadFile(file)
;	
;	section = Lower(section)
;	
;	Local n%=0
;	While Not Eof(f)
;		Local strtemp$ = ReadLine(f)
;		n=n+1
;		If Left(strtemp,1) = "[" Then
;			strtemp$ = Lower(strtemp)
;			Temp = Instr(strtemp, section)
;			If Temp>0 Then
;				If Mid(strtemp, Temp-1, 1)="[" Or Mid(strtemp, Temp-1, 1)="|" Then
;					CloseFile f
;					Return n
;				EndIf
;			EndIf
;		EndIf
;	Wend
;	
;	CloseFile f
;End Function
;
;
;
;Function PutINIValue%(file$, INI_sSection$, INI_sKey$, INI_sValue$)
;	
;	; Returns: True (Success) Or False (Failed)
;	
;	INI_sSection = "[" + Trim$(INI_sSection) + "]"
;	Local INI_sUpperSection$ = Upper$(INI_sSection)
;	INI_sKey = Trim$(INI_sKey)
;	INI_sValue = Trim$(INI_sValue)
;	Local INI_sFilename$ = file$
;	
;	; Retrieve the INI Data (If it exists)
;	
;	Local INI_sContents$ = INI_FileToString(INI_sFilename)
;	
;		; (Re)Create the INI file updating/adding the SECTION, KEY And VALUE
;	
;	Local INI_bWrittenKey% = False
;	Local INI_bSectionFound% = False
;	Local INI_sCurrentSection$ = ""
;	
;	Local INI_lFileHandle% = WriteFile(INI_sFilename)
;	If INI_lFileHandle = 0 Then Return False ; Create file failed!
;	
;	Local INI_lOldPos% = 1
;	Local INI_lPos% = Instr(INI_sContents, Chr$(0))
;	
;	While (INI_lPos <> 0)
;		
;		Local INI_sTemp$ = Mid$(INI_sContents, INI_lOldPos, (INI_lPos - INI_lOldPos))
;		
;		If (INI_sTemp <> "") Then
;			
;			If Left$(INI_sTemp, 1) = "[" And Right$(INI_sTemp, 1) = "]" Then
;				
;					; Process SECTION
;				
;				If (INI_sCurrentSection = INI_sUpperSection) And (INI_bWrittenKey = False) Then
;					INI_bWrittenKey = INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
;				End If
;				INI_sCurrentSection = Upper$(INI_CreateSection(INI_lFileHandle, INI_sTemp))
;				If (INI_sCurrentSection = INI_sUpperSection) Then INI_bSectionFound = True
;				
;			Else
;				DebugLog INI_sTemp
;				If Left(INI_sTemp, 1) = ":" Then
;					WriteLine INI_lFileHandle, INI_sTemp
;				Else
;						; KEY=VALUE				
;					Local lEqualsPos% = Instr(INI_sTemp, "=")
;					If (lEqualsPos <> 0) Then
;						If (INI_sCurrentSection = INI_sUpperSection) And (Upper$(Trim$(Left$(INI_sTemp, (lEqualsPos - 1)))) = Upper$(INI_sKey)) Then
;							If (INI_sValue <> "") Then INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
;							INI_bWrittenKey = True
;						Else
;							WriteLine INI_lFileHandle, INI_sTemp
;						End If
;					End If
;				EndIf
;				
;			End If
;			
;		End If
;		
;			; Move through the INI file...
;		
;		INI_lOldPos = INI_lPos + 1
;		INI_lPos% = Instr(INI_sContents, Chr$(0), INI_lOldPos)
;		
;	Wend
;	
;		; KEY wasn;t found in the INI file - Append a New SECTION If required And create our KEY=VALUE Line
;	
;	If (INI_bWrittenKey = False) Then
;		If (INI_bSectionFound = False) Then INI_CreateSection INI_lFileHandle, INI_sSection
;		INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
;	End If
;	
;	CloseFile INI_lFileHandle
;	
;	Return True ; Success
;	
;End Function
;
;Function INI_FileToString$(INI_sFilename$)
;	
;	Local INI_sString$ = ""
;	Local INI_lFileHandle%= ReadFile(INI_sFilename)
;	If INI_lFileHandle <> 0 Then
;		While Not(Eof(INI_lFileHandle))
;			INI_sString = INI_sString + ReadLine$(INI_lFileHandle) + Chr$(0)
;		Wend
;		CloseFile INI_lFileHandle
;	End If
;	Return INI_sString
;	
;End Function
;
;Function INI_CreateSection$(INI_lFileHandle%, INI_sNewSection$)
;	
;	If FilePos(INI_lFileHandle) <> 0 Then WriteLine INI_lFileHandle, "" ; Blank Line between sections
;	WriteLine INI_lFileHandle, INI_sNewSection
;	Return INI_sNewSection
;	
;End Function
;
;Function INI_CreateKey%(INI_lFileHandle%, INI_sKey$, INI_sValue$)
;	
;	WriteLine INI_lFileHandle, INI_sKey + " = " + INI_sValue
;	Return True
;	
;End Function
;
;
;Type Materials
;	Field name$
;	Field Diff
;	Field Bump
;	
;	Field StepSound%
;End Type
;
;Function LoadMaterials(file$)
;	;If Not BumpEnabled Then Return
;	
;	Local TemporaryString$, Temp%, i%, n%
;	Local mat.Materials = Null
;	Local StrTemp$ = ""
;	
;	Local f = OpenFile(file)
;	
;	While Not Eof(f)
;		TemporaryString = Trim(ReadLine(f))
;		If Left(TemporaryString,1) = "[" Then
;			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
;			
;			mat.Materials = New Materials
;			
;			mat\name = Lower(TemporaryString)
;			
;			If BumpEnabled Then
;				StrTemp = GetINIString(file, TemporaryString, "bump")
;				If StrTemp <> "" Then 
;					mat\Bump =  LoadTexture(StrTemp)
;					;TextureBlend mat\Bump,4
;					;SetCubeMode mat\Bump,2
;					
;					TextureBlend mat\Bump, FE_BUMP				
;				EndIf
;			EndIf
;			
;			mat\StepSound = (GetINIInt(file, TemporaryString, "stepsound")+1)
;		EndIf
;	Wend
;	
;	CloseFile f
;	
;End Function
;
;
;;RMESH STUFF;;;;
;
;Function StripFilename$(file$)
;	Local mi$=""
;	Local lastSlash%=0
;	If Len(file)>0
;		For i%=1 To Len(file)
;			mi=Mid(file$,i,1)
;			If mi="\" Or mi="/" Then
;				lastSlash=i
;			EndIf
;		Next
;	EndIf
;	
;	Return Left(file,lastSlash)
;End Function
;
;Function GetTextureFromCache%(name$)
;	For tc.Materials=Each Materials
;		If tc\name = name Then Return tc\Diff
;	Next
;	Return 0
;End Function
;
;Function GetBumpFromCache%(name$)
;	For tc.Materials=Each Materials
;		If tc\name = name Then Return tc\Bump
;	Next
;	Return 0
;End Function
;
;Function GetCache.Materials(name$)
;	For tc.Materials=Each Materials
;		If tc\name = name Then Return tc
;	Next
;	Return Null
;End Function
;
;Function AddTextureToCache(texture%)
;	Local tc.Materials=GetCache(StripPath(TextureName(texture)))
;	If tc.Materials=Null Then
;		tc.Materials=New Materials
;		tc\name=StripPath(TextureName(texture))
;		Local temp$=GetINIString("Data\materials.ini",tc\name,"bump")
;		If temp<>"" Then
;			tc\Bump=LoadTexture(temp)
;			TextureBlend tc\Bump,FE_BUMP
;		Else
;			tc\Bump=0
;		EndIf
;		tc\Diff=0
;	EndIf
;	If tc\Diff=0 Then tc\Diff=texture
;End Function
;
;Function ClearTextureCache()
;	For tc.Materials=Each Materials
;		If tc\Diff<>0 Then FreeTexture tc\Diff
;		If tc\Bump<>0 Then FreeTexture tc\Bump
;		Delete tc
;	Next
;End Function
;
;Function FreeTextureCache()
;	For tc.Materials=Each Materials
;		If tc\Diff<>0 Then FreeTexture tc\Diff
;		If tc\Bump<>0 Then FreeTexture tc\Bump
;		tc\Diff = 0 : tc\Bump = 0
;	Next
;End Function
;
;;-----------;;;;
;
;Function StripPath$(file$) 
;	
;	If Len(file$)>0 
;		
;		For i=Len(file$) To 1 Step -1 
;			
;			mi$=Mid$(file$,i,1) 
;			If mi$="\" Or mi$="/" Then Return name$ Else name$=mi$+name$ 
;			
;		Next 
;		
;	EndIf 
;	
;	Return name$ 
;	
;End Function 
;
;Function Piece$(s$,entry,char$=" ")
;	While Instr(s,char+char)
;		s=Replace(s,char+char,char)
;	Wend
;	For n=1 To entry-1
;		p=Instr(s,char)
;		s=Right(s,Len(s)-p)
;	Next
;	p=Instr(s,char)
;	If p<1
;		a$=s
;	Else
;		a=Left(s,p-1)
;	EndIf
;	Return a
;End Function
;
;Function KeyValue$(entity,key$,defaultvalue$="")
;	properties$=EntityName(entity)
;	properties$=Replace(properties$,Chr(13),"")
;	key$=Lower(key)
;	Repeat
;		p=Instr(properties,Chr(10))
;		If p Then test$=(Left(properties,p-1)) Else test=properties
;		testkey$=Piece(test,1,"=")
;		testkey=Trim(testkey)
;		testkey=Replace(testkey,Chr(34),"")
;		testkey=Lower(testkey)
;		If testkey=key Then
;			value$=Piece(test,2,"=")
;			value$=Trim(value$)
;			value$=Replace(value$,Chr(34),"")
;			Return value
;		EndIf
;		If Not p Then Return defaultvalue$
;		properties=Right(properties,Len(properties)-p)
;	Forever 
;End Function
;
;Const FPSfactor# = 1.0
;
;ChangeDir ".."
;
;Graphics3D 800,600,32,2
;Init3DMenu()
;SetBuffer BackBuffer()
;While Not KeyHit(1)
;	Update3DMenu()
;	Flip
;	Delay 8
;Wend
;
;EndGraphics
;End
;~IDEal Editor Parameters:
;~C#Blitz3D