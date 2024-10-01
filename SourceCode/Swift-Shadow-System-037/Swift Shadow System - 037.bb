; -------------------------------------------------------------------------------------------------------------------
; Swift Shadow System - Copyright 2002 Shawn C. Swift
; -------------------------------------------------------------------------------------------------------------------

; To do:
; Fix offset mesh being offset in texture and calculating wrong scale issue.

; This is a list of all objects which can cast shadows, and the properties of the shadows which they cast.
Type Shadow_Caster

	Field Entity		; The entity which is casting a shadow.
	Field Texture		; Only used for fake textured shadows.
	Field Resolution	; The resolution to use for rendered shadow maps.
	Field Translucent	; Specifies whether the shadow caster is translucent ot not so we can render a special shadow.
	Field Max_X#		; This is a special point which uses the maximum X Y and Z values of the points in the casting mesh.
	Field Max_Y#		; When scaled, this point should represent the maximum possible radius of any point in the model.
	Field Max_Z#			
	Field Radius#		; Specifies the radius of the shadow casting object.  Recalculated each frame.
	Field Diameter#		; Specifies the diameter of the shadow casting object.  Recalculated each frame.
	Field RenderTexture	; Specifies whether or not this shadow uses rendered shadow maps.

End Type


; This is a list of all the objects which can receive shadows.
Type Shadow_Receiver

	Field Entity
	Field Radius#
	
End Type


; This is a list of all the lights which cast shadows.
Type Light_Caster

	Field Entity 
	Field Range#

End Type	


; This is a list of all the shadow meshes which have been created during an update.
Type Shadow

	Field Mesh

End Type


; This is a list of all the light and shadow caster pairs.
; For each caster there is one texture rendered per light.
Type Caster_Set 

	Field Caster.Shadow_Caster
	Field Light.Light_Caster
	Field Texture					; This is set to 0 if there is no texture associated with this pair.
	Field Texture_Pitch#			; This is the angle the caster was at relative to the light source when the	
	Field Texture_Yaw#				; texture was rendered.  When any of these values exceeds Texture_Angle_Epsilon#
	Field Texture_Roll#				; a new texture will be rendered.

End Type	


; This is a list of all static shadow meshes.
; These meshes are created and textured once using the current light sources, and then never rendered again.
Type Static_Shadow

	Field Mesh				; The shadow's mesh.
	Field Casting_Entity	; The entity which cast this shadow.

End Type


; This is a list of all static shadow textures.
; Each caster only needs one texture per light source, so we seperate this from the meshes.
Type Static_Shadow_Texture

	Field Texture
	Field Casting_Entity

End Type


; This is a list of all objects which can cast shadows, and the properties of the shadows which they cast.
Type Static_Shadow_Caster

	Field Entity		; The entity which is casting a shadow.
	Field Resolution	; The resolution to use for rendered shadow maps.
	Field Radius#		; Specifies the radius of the shadow casting object.
	Field Diameter#		; Specifies the diameter of the shadow casting object.

End Type


; This is the number of polygons which were added to shadow meshes this update.
Global Shadow_Poly_Count = 0	
	

; Set this to a value greater than 0 if you wish to nudge the shadow meshes in the direction of the normal
; of the underlying surface.
;
; Nudging the shadow takes care of z fighting issues, but does take a little more processing time.  If you are
; rendering your shadows in a lighting pass before you render your world geometry, then you can probably set 
; this to 0.
;
; The amount you should nudge the shadows depends on the scale of your world.  If 1 unit = 1 meter in your game
; world, then you should set shadow_nudge to be really small!
Global Shadow_Nudge# = 0.001


; You should set these to the current ambient light color in your world.
Global Shadow_Ambient_Light_R = 0
Global Shadow_Ambient_Light_G = 0
Global Shadow_Ambient_Light_B = 0


; I'm not sure why but I have to adjust the ambient light up from the setting used for the world to match
; the shadows ambient light to it.  I think DX does something funky to the ambient light level on models.
Global Shadow_Ambient_Multiplier# = 1.25


; This is the amount which the angle of the caster has to change relative to it's light source before the shadow map
; is rerendered.  You should not need to change this value, I've set it to an optimal one, but if you decide you
; don't mind if your shadows jump around a little in extreme cases, and you want to improve performance by a few fps
; on slow PC's, then you can adjust this value.  You can change this value at runtime just like all the other values
; here.
Global Texture_Angle_Epsilon# = 0.8


; This is the amount the add-caster functions multiply the true radius of the mesh by. 
; We fudge the numbers a bit so that when we render the texture the model doesn't touch the edge.  If it did, then
; the UV clamping would create streaks across the whole shadow mesh where there is no shadow.
Global Caster_Radius_Fudge_Factor# = 1.05


; -------------------------------------------------------------------------------------------------------------------
; This function sets up a new rendered shadow caster.
;
; Caster		= Mesh to cast shadows.
; Resolution	= The resolution for the shadow map to be rendered for this object.  256 is good for most shadows.
; Translucent	= Set this to 1 if you want an effect like light passing through a colored glass object.
;
; Note that shadows will cast onto all surfaces facing the light source which are occluded by the shadow caster, and
; defined as shadow receivers.
;
; This means that in a level with one room above another, if the character is in the top room, they will cast a 
; shadow onto the floor of that room, and onto the floor of the room below.
;
; You can combat this by setting the RANGE# parameter for your shadow lights.  
;
; By specifying a range which is small, you will speed up rendering by not rendering shadows too far from a light
; source, and prevent shadows from casting onto unwanted surfaces.
; -------------------------------------------------------------------------------------------------------------------
Function Cast_Shadow(Caster, Resolution=256, Translucent=0)


	ThisCaster.Shadow_Caster = New Shadow_Caster	

	ThisCaster\RenderTexture = 1		

	ThisCaster\Entity     = Caster
	ThisCaster\Resolution = Resolution

	ThisCaster\Translucent = Translucent


	; Get the radius of shadow which we need to create to cast a shadow from this caster.
	
		; Loop through all vertices in all surfaces of the caster.
		Surfaces = CountSurfaces(ThisCaster\Entity)
		For LOOP_Surface = 1 To Surfaces

			Surface_Handle = GetSurface(ThisCaster\Entity, LOOP_Surface)
			
			Verts = CountVertices(Surface_Handle) 
			For LOOP_Verts = 0 To Verts-1
			
				VX# = Abs(VertexX#(Surface_Handle, LOOP_Verts))
				VY# = Abs(VertexY#(Surface_Handle, LOOP_Verts))
				VZ# = Abs(VertexZ#(Surface_Handle, LOOP_Verts))
			
				If (VX# > ThisCaster\Max_X#) Then ThisCaster\Max_X# = VX#
				If (VY# > ThisCaster\Max_Y#) Then ThisCaster\Max_Y# = VY#
				If (VZ# > ThisCaster\Max_Z#) Then ThisCaster\Max_Z# = VZ#
				
			Next
		
		Next
	

	; Set up a new caster set for each light.
	For ThisLight.Light_Caster = Each Light_Caster

		ThisSet.Caster_Set = New Caster_Set 

		ThisSet\Caster = ThisCaster
		ThisSet\Light = ThisLight
		
		; Create a UV clamped and unmanaged texture for this shadow.
		ThisSet\Texture = CreateTexture(ThisCaster\Resolution, ThisCaster\Resolution, 16+32+256)
				
		; Set the texture blend mode to add, which is the mode we need so the vertex colors can fade the shadow.
		TextureBlend ThisSet\Texture, 3

	Next


	; Create one new shadow mesh for each recevier/light pair.
	For Loop.Shadow_Receiver = Each Shadow_Receiver
	
		For Loop2.Light_Caster = Each Light_Caster

			ThisShadow.Shadow = New Shadow
			ThisShadow\Mesh = CreateMesh()
			SURFACE_Shadow  = CreateSurface(ThisShadow\Mesh)
	
		Next
			
	Next	


End Function


; -------------------------------------------------------------------------------------------------------------------
; This function sets up a new shadow caster which casts a predefined texture you specify, rather than casting a 
; shadow which is rendered in realtime.
;
; Use this if you want to use fake round shadows for certain objects.
;
; The rendering of the shadow texture is the most expensive part of the shadow casting process, so these shadows
; render MUCH faster than a regular shadow.
;
; Caster      = Mesh to cast shadows.
; Texture     = The texture to use for the shadow.
;
; To create a good shadow texture, create a white background and place a black circle in the middle which fills the
; whole image, except for a 1 pixel wide border around the edge.
;
; When you load the texture, you MUST enable UV clamping and disable mipmapping.
;
; Try creating colored shadow textures for interesting effects!
;
; Important Note:
; Ambient light will not affect the color of textured shadows!
; -------------------------------------------------------------------------------------------------------------------
Function Cast_Textured_Shadow(Caster, Texture)


	ThisCaster.Shadow_Caster = New Shadow_Caster	

	ThisCaster\Entity	= Caster
	ThisCaster\Texture	= Texture

	; Get the radius of shadow which we need to create to cast a shadow from this caster.

		; Loop through all vertices in all surfaces of the caster.
		Surfaces = CountSurfaces(ThisCaster\Entity)
		For LOOP_Surface = 1 To Surfaces

			Surface_Handle = GetSurface(ThisCaster\Entity, LOOP_Surface)
			
			Verts = CountVertices(Surface_Handle) - 1
			For LOOP_Verts = 0 To Verts-1
			
				VX# = Abs(VertexX#(Surface_Handle, LOOP_Verts))
				VY# = Abs(VertexY#(Surface_Handle, LOOP_Verts))
				VZ# = Abs(VertexZ#(Surface_Handle, LOOP_Verts))
				
				If (VX# > ThisCaster\Max_X#) Then ThisCaster\Max_X# = VX#
				If (VY# > ThisCaster\Max_Y#) Then ThisCaster\Max_Y# = VY#
				If (VZ# > ThisCaster\Max_Z#) Then ThisCaster\Max_Z# = VZ#
				
			Next
		
		Next
	

	; Set the texture's blend mode to add, which is the mode we need so the vertex colors can fade the shadow.
	TextureBlend Texture, 3


	; Set up a new caster set for each light.
	For ThisLight.Light_Caster = Each Light_Caster

		ThisSet.Caster_Set = New Caster_Set 

		ThisSet\Caster = ThisCaster
		ThisSet\Light = ThisLight
		
		; Fake shadows don't render textures.
		ThisSet\Texture = 0

	Next


	; Create one new shadow mesh for each recevier/light pair.
	For Loop.Shadow_Receiver = Each Shadow_Receiver
	
		For Loop2.Light_Caster = Each Light_Caster

			ThisShadow.Shadow = New Shadow
			ThisShadow\Mesh = CreateMesh()
			SURFACE_Shadow  = CreateSurface(ThisShadow\Mesh)
	
		Next
			
	Next	


End Function



; -------------------------------------------------------------------------------------------------------------------
; This function sets up a new rendered static shadow caster.
;
; Caster		= Mesh to cast shadows.
; Resolution	= The resolution for the shadow map to be rendered for this object.  256 is good for most shadows.
;
; Note that shadows will cast onto all surfaces facing the light source which are occluded by the shadow caster, and
; defined as shadow receivers.
;
; This means that in a level with one room above another, if the character is in the top room, they will cast a 
; shadow onto the floor of that room, and onto the floor of the room below.
;
; You can combat this by setting the RANGE# parameter for your shadow lights.  
;
; By specifying a range which is small, you will speed up rendering by not rendering shadows too far from a light
; source, and prevent shadows from casting onto unwanted surfaces.
; -------------------------------------------------------------------------------------------------------------------
Function Cast_Static_Shadow(Caster, Resolution=256)


	ThisCaster.Static_Shadow_Caster = New Static_Shadow_Caster	

	ThisCaster\Entity     = Caster
	ThisCaster\Resolution = Resolution


	; Get the radius of shadow which we need to create to cast a shadow from this caster.

		; Loop through all vertices in all surfaces of the caster.
		Surfaces = CountSurfaces(ThisCaster\Entity)
		For LOOP_Surface = 1 To Surfaces

			Surface_Handle = GetSurface(ThisCaster\Entity, LOOP_Surface)
			
			Verts = CountVertices(Surface_Handle) - 1
			For LOOP_Verts = 0 To Verts-1
			
				VX# = Abs(VertexX#(Surface_Handle, LOOP_Verts))
				VY# = Abs(VertexY#(Surface_Handle, LOOP_Verts))
				VZ# = Abs(VertexZ#(Surface_Handle, LOOP_Verts))

				If (VX# > Max_X#) Then Max_X# = VX#
				If (VY# > Max_Y#) Then Max_Y# = VY#
				If (VZ# > Max_Z#) Then Max_Z# = VZ#
				
			Next
		
		Next


	; Store the caster's radius and diameter.
	
		ScaleX# = EntityScaleX#(ThisCaster\Entity)
		ScaleY# = EntityScaleY#(ThisCaster\Entity)
		ScaleZ# = EntityScaleZ#(ThisCaster\Entity)
	
		Sx# = Max_X# * ScaleX#
		Sy# = Max_Y# * ScaleY#
		Sz# = Max_Z# * ScaleZ#
	
		; Fudge radius a little bit so that the shadow texture doesn't render with the object touching the edge of the texture.
		ThisCaster\Radius# = Sqr(Sx#^2.0 + Sy#^2.0 + Sz#^2.0) * Caster_Radius_Fudge_Factor#
		ThisCaster\Diameter# = ThisCaster\Radius# * 2.0


End Function


; -------------------------------------------------------------------------------------------------------------------
; This function sets a mesh as being capable of receiving a shadow.
;
; Animated meshes cannot receive shadows because Blitz does not return the current locations of it's vertices, it
; returns the locations of them in the model's default pose.
;
; Remember that while you can rotate and position the entity receving a shadow, you CANNOT scale it.  
; If you need to scale your receiver, you'll have to scale the whole mesh.
;
; Another important thing to note is that children of an entity receiving shadows will not automatically receive
; shadows themselves.  You must set each entity to recieve shadows individually.
;
; Lastly, all shadow casting objects will cast shadows onto all objets marked as receivers.
; -------------------------------------------------------------------------------------------------------------------
Function Receive_Shadow(Receiver)

	ThisReceiver.Shadow_Receiver = New Shadow_Receiver
	ThisReceiver\Entity = Receiver

	; Get the radius of the sphere which completely encloses this receiver.

		; Loop through all vertices in all surfaces of the reciever.
		Surfaces = CountSurfaces(ThisReceiver\Entity)
		For LOOP_Surface = 1 To Surfaces

			Surface_Handle = GetSurface(ThisReceiver\Entity, LOOP_Surface)
			
			Verts = CountVertices(Surface_Handle) - 1
			For LOOP_Verts = 0 To Verts-1
			
				VX# = VertexX#(Surface_Handle, LOOP_Verts)
				VY# = VertexY#(Surface_Handle, LOOP_Verts)
				VZ# = VertexZ#(Surface_Handle, LOOP_Verts)
				
				; Compute vertex distance^2
				VR# = VX#*VX# + VY#*VY# + VZ#*VZ#
				
				If Radius# < VR# Then Radius# = VR#
				
			Next
		
		Next

	
	; Store the receiver's true radius.
	ThisReceiver\Radius# = Sqr(Radius#)
	

	; Create one shadow mesh for this receiver for every caster/light pair.
	For Loop.Caster_Set = Each Caster_Set

		ThisShadow.Shadow = New Shadow
		ThisShadow\Mesh = CreateMesh()
		SURFACE_Shadow  = CreateSurface(ThisShadow\Mesh)
		
	Next	
			
End Function	

; -------------------------------------------------------------------------------------------------------------------
; This function creates a new light source.
;
; Light_Entity is the pointer to a light you have created in your world.  That light is accessed to get it's
; location in space each time you update the shadows.  This could also be a pivot, or any other entity, though it
; would be a rare case indeed where you would want to point it to anything but an actual light source.
;
; Light_Range is the range at which you want the shadow to fade out completely.  Beyond this range, the shadow will
; not be calculated.  This value should not be larger than the actual light's range, as that would cause the shadow
; to render at ranges beyond that which the light can reach.  It would be a good idea to keep your light ranges small
; so that shadows are only rendered for lights which are very close to your objects.  To do otherwise would create
; a lot of slowdown in your game.  Shadows are fairly expensive to render, and you do not want to have six monsters
; in a room all casting shadows from two or more light sources... especially if said monsters and their environment
; are high-poly.  This system is not designed to handle many shadows at once.  Btw, lights fall off with the inverse 
; square of the distance.
;
; When you delete a light which your shadow system is using, you must also delete the light source via the shadow
; system or your game will crash when you next update the shadows.
;
; Only POINT lights are supported for casting shadows.  Shadows will not cast properly from directional or spot
; lights, and may even crash your game.
; -------------------------------------------------------------------------------------------------------------------
Function Cast_Light(Entity, Range#)


	ThisLight.Light_Caster = New Light_Caster
	
	ThisLight\Entity = Entity 
	ThisLight\Range# = Range#


	; Set up a new caster set for each caster casting shadows from this light.
	For ThisCaster.Shadow_Caster = Each Shadow_Caster

		ThisSet.Caster_Set = New Caster_Set 

		ThisSet\Caster = ThisCaster
		ThisSet\Light = ThisLight

		If ThisCaster\RenderTexture = 1		

			; Create a UV clamped and unmanaged texture for this shadow.
			ThisSet\Texture = CreateTexture(ThisCaster\Resolution, ThisCaster\Resolution, 16+32+256)
				
			; Set the texture blend mode to add, which is the mode we need so the vertex colors can fade the shadow.
			TextureBlend ThisSet\Texture, 3
	
		Else
			
			; Fake shadows don't render textures.
			ThisSet\Texture = 0
			
		EndIf

	Next


	; Create one new shadow mesh for each caster/recevier pair.
	For Loop.Shadow_Receiver = Each Shadow_Receiver
	
		For Loop2.Shadow_Caster = Each Shadow_Caster

			ThisShadow.Shadow = New Shadow
			ThisShadow\Mesh = CreateMesh()
			SURFACE_Shadow  = CreateSurface(ThisShadow\Mesh)
	
		Next
			
	Next	


End Function


; -------------------------------------------------------------------------------------------------------------------
; This function deletes all old shadows and creates a shadow mesh for each shadow casting object.
;
; Note:
; Shadows only need to be repositioned and recreated each time the scene is rendered, 
; not every time the physics are updated.
;
; Make sure if you are using my HUD system that you HideEntity HUD_Camera before calling update_shadows() or
; update_static_shadows(), and then ShowEntity HUD_Camera immediately afterwards.  Otherwise the HUD will not
; render, and may corrupt or slow down shadow rendering.
;
; Current_Camera = The camera viewing the scene.  Neccessary so we can hide it while we render the shadow textures.
; -------------------------------------------------------------------------------------------------------------------
Function Update_Shadows(Current_Camera)


	; Turn off the main camera view.
		HideEntity Current_Camera
		;CameraProjMode Current_Camera, 0

	; Reset the count of the number of polys in all the shadows combined.
		Shadow_Poly_Count = 0


	; Make a blank texture we can use to un-texture the shadow meshes with.
		;Shadow_Caster_Texture = CreateTexture(32, 32, 0)
		;TextureBlend Shadow_Caster_Texture, 0


	; Create a camera to render the shadows with.
	
		; Create a new camera.
		Shadow_Cam = CreateCamera()
		Shadow_Cam_Position#  = -65535.0
		Shadow_Caster_Offset# = -16.0

		; Set the camera's range to be very small so as to reduce the possiblity of extra objects making it into the scene.
		CameraRange Shadow_Cam, 0.1, 100

		; Set the screen clear mode, and set the clear color to white.
		CameraClsMode Shadow_Cam, True, True
		CameraClsColor Shadow_Cam, 255, 255, 255
		
		; Set the camera to zoom in on the object to reduce perspective error from the object being too close to the camera.
		CameraZoom Shadow_Cam, 16.0
				
		; Aim camera straight down.	
		RotateEntity Shadow_Cam, 90, 0, 0, True

		; Position camera very far down in world so as to probably not have any other objects visible.
		PositionEntity Shadow_Cam, 0, Shadow_Cam_Position#, 0, True


	; Create a pivot to represent the light source.
		Light_Pivot = CreatePivot()


	; Create a pivot to represent the shadow caster.
		Caster_Pivot = CreatePivot()


	; Clear all the shadow meshes.
	For ThisShadow.Shadow = Each Shadow

		; Find the surface of this shadow mesh.
		SURFACE_Shadow = GetSurface(ThisShadow\Mesh, 1)
				
		; Clear the surface of this shadow mesh.
		ClearSurface SURFACE_Shadow

	Next


	; Get the pointer to the first available shadow mesh.
	; Care has been taken to make sure there are EXACTLY enough shadow meshes for the number
	; of casters, receivers, and lights we have.
	ThisShadow.Shadow = First Shadow


	; Loop through all the pairs of light sources and shadow casters.
	For ThisSet.Caster_Set = Each Caster_Set


		ThisLight.Light_Caster   = ThisSet\Light
		ThisCaster.Shadow_Caster = ThisSet\Caster


		; Get the location of the light in world space.
		Light_X# = EntityX#(ThisLight\Entity, True)
		Light_Y# = EntityY#(ThisLight\Entity, True)
		Light_Z# = EntityZ#(ThisLight\Entity, True)


		; Get the location of the shadow caster in world space.	
		Caster_X# = EntityX#(ThisCaster\Entity, True)
		Caster_Y# = EntityY#(ThisCaster\Entity, True)
		Caster_Z# = EntityZ#(ThisCaster\Entity, True)
		

		; Calculate the distance between the light and the caster.
		Light_To_Caster_Distance# = Sqr((Light_X#-Caster_X#)*(Light_X#-Caster_X#) + (Light_Y#-Caster_Y#)*(Light_Y#-Caster_Y#) + (Light_Z#-Caster_Z#)*(Light_Z#-Caster_Z#))


		; Is this shadow caster near enough to the light source to cast a shadow at all?
		If Light_To_Caster_Distance# <= ThisLight\Range#


			; Calculate this caster's radius.
				
				Caster_Scale_X# = EntityScaleX#(ThisCaster\Entity)
				Caster_Scale_Y# = EntityScaleY#(ThisCaster\Entity)
				Caster_Scale_Z# = EntityScaleZ#(ThisCaster\Entity)
	
				Sx# = ThisCaster\Max_X# * Caster_Scale_X#
				Sy# = ThisCaster\Max_Y# * Caster_Scale_Y#
				Sz# = ThisCaster\Max_Z# * Caster_Scale_Z#
	
				; Fudge radius a little bit so that the shadow texture doesn't render with the object touching the edge of the texture.
				ThisCaster\Radius# = Sqr(Sx#*Sx# + Sy#*Sy# + Sz#*Sz#) * Caster_Radius_Fudge_Factor#
				ThisCaster\Diameter# = ThisCaster\Radius# * 2.0

	
			; Position the light pivot where the light is in the world.
			PositionEntity Light_Pivot, Light_X#, Light_Y#, Light_Z#, True
				

			; Point light_pivot at the caster.								
			PointEntity Light_Pivot, ThisCaster\Entity


			; Calculate the normal of the light's ray in world space.
			; We use this normal to calculate how to project the points in the world onto the light's plane.
			;
			; Optimization note: 
			; The normal of the caster plane will be the same as this normal, so we will not need to calculate
			; this normal.
			; (A, B, C) will be the normal of the plane.
			TFormNormal 0, 0, 1, Light_Pivot, 0
			Light_NX# = TFormedX#()
			Light_NY# = TFormedY#()
			Light_NZ# = TFormedZ#()
		

			; Calculate the plane equations for the 4 planes which make up this light's view frustrum.

				; Calculate the locations, in world space, of six points around the light which we
				; can use to define the four planes for the view frustrum.

					; Top left.
					TFormPoint -ThisCaster\Radius#, ThisCaster\Radius#, 0, Light_Pivot, 0
					X1# = TFormedX#()
					Y1# = TFormedY#()
					Z1# = TFormedZ#()
					
					; Top Right
					TFormPoint ThisCaster\Radius#, ThisCaster\Radius#, 0, Light_Pivot, 0
					X2# = TFormedX#()
					Y2# = TFormedY#()
					Z2# = TFormedZ#()

					; Bottom Left 
					TFormPoint -ThisCaster\Radius#, -ThisCaster\Radius#, 0, Light_Pivot, 0		
					X3# = TFormedX#()
					Y3# = TFormedY#()
					Z3# = TFormedZ#()

					; Top Left Forward
					TFormPoint -ThisCaster\Radius#, ThisCaster\Radius#, 1, Light_Pivot, 0									
					X4# = TFormedX#()
					Y4# = TFormedY#()
					Z4# = TFormedZ#()

					; Bottom Right
					TFormPoint ThisCaster\Radius#, -ThisCaster\Radius#, 0, Light_Pivot, 0
					X5# = TFormedX#()
					Y5# = TFormedY#()
					Z5# = TFormedZ#()

					; Bottom
					TFormPoint 0, -ThisCaster\Radius#, 0, Light_Pivot, 0
					X6# = TFormedX#()
					Y6# = TFormedY#()
					Z6# = TFormedZ#()
					
					; Right
					TFormPoint ThisCaster\Radius#, 0, 0, Light_Pivot, 0
					X7# = TFormedX#()
					Y7# = TFormedY#()
					Z7# = TFormedZ#()

					; Bottom Right Forward
					TFormPoint ThisCaster\Radius#, -ThisCaster\Radius#, 1, Light_Pivot, 0
					X8# = TFormedX#()
					Y8# = TFormedY#()
					Z8# = TFormedZ#()
					
				
				; Calculate the plane equations for each one of the faces.		
				; Points provided to the plane in counterclockwise order so the normal points up.

					; Top plane
					; (4, 1, 2)
									
					Ax# = X1#-X4#
					Ay# = Y1#-Y4#
					Az# = Z1#-Z4#

					Bx# = X2#-X4#
					By# = Y2#-Y4#
					Bz# = Z2#-Z4#

					Nx# = (Ay# * Bz#) - (By# * Az#)
					Ny# = (Az# * Bx#) - (Bz# * Ax#) 	
					Nz# = (Ax# * By#) - (Bx# * Ay#)
	
					Nl# = Sqr(Nx#*Nx# + Ny#*Ny# + Nz#*Nz#)
	
					A1# = Nx# / Nl#
					B1# = Ny# / Nl#
					C1# = Nz# / Nl#
	
					D1# = -((A1# * X4#) + (B1# * Y4#) + (C1# * Z4#))


					; Bottom plane
					; (8, 5, 6)

					Ax# = X5#-X8#
					Ay# = Y5#-Y8#
					Az# = Z5#-Z8#

					Bx# = X6#-X8#
					By# = Y6#-Y8#
					Bz# = Z6#-Z8#

					Nx# = (Ay# * Bz#) - (By# * Az#)
					Ny# = (Az# * Bx#) - (Bz# * Ax#) 	
					Nz# = (Ax# * By#) - (Bx# * Ay#)
	
					Nl# = Sqr(Nx#*Nx# + Ny#*Ny# + Nz#*Nz#)
	
					A2# = Nx# / Nl#
					B2# = Ny# / Nl#
					C2# = Nz# / Nl#
	
					D2# = -((A2# * X8#) + (B2# * Y8#) + (C2# * Z8#))

									
					; Left plane
					; (1, 4, 3)
					
					Ax# = X4#-X1#
					Ay# = Y4#-Y1#
					Az# = Z4#-Z1#

					Bx# = X3#-X1#
					By# = Y3#-Y1#
					Bz# = Z3#-Z1#

					Nx# = (Ay# * Bz#) - (By# * Az#)
					Ny# = (Az# * Bx#) - (Bz# * Ax#) 	
					Nz# = (Ax# * By#) - (Bx# * Ay#)
	
					Nl# = Sqr(Nx#*Nx# + Ny#*Ny# + Nz#*Nz#)
	
					A3# = Nx# / Nl#
					B3# = Ny# / Nl#
					C3# = Nz# / Nl#
	
					D3# = -((A3# * X1#) + (B3# * Y1#) + (C3# * Z1#))


					; Right plane
					; (7, 5, 8)

					Ax# = X5#-X7#
					Ay# = Y5#-Y7#
					Az# = Z5#-Z7#

					Bx# = X8#-X7#
					By# = Y8#-Y7#
					Bz# = Z8#-Z7#

					Nx# = (Ay# * Bz#) - (By# * Az#)
					Ny# = (Az# * Bx#) - (Bz# * Ax#) 	
					Nz# = (Ax# * By#) - (Bx# * Ay#)
	
					Nl# = Sqr(Nx#*Nx# + Ny#*Ny# + Nz#*Nz#)
	
					A4# = Nx# / Nl#
					B4# = Ny# / Nl#
					C4# = Nz# / Nl#
	
					D4# = -((A4# * X7#) + (B4# * Y7#) + (C4# * Z7#))


					; Back plane
					; (2, 1, 3)

					Ax# = X1#-X2#
					Ay# = Y1#-Y2#
					Az# = Z1#-Z2#

					Bx# = X3#-X2#
					By# = Y3#-Y2#
					Bz# = Z3#-Z2#

					Nx# = (Ay# * Bz#) - (By# * Az#)
					Ny# = (Az# * Bx#) - (Bz# * Ax#) 	
					Nz# = (Ax# * By#) - (Bx# * Ay#)
	
					Nl# = Sqr(Nx#*Nx# + Ny#*Ny# + Nz#*Nz#)
	
					A5# = Nx# / Nl#
					B5# = Ny# / Nl#
					C5# = Nz# / Nl#
	
					D5# = -((A5# * X2#) + (B5# * Y2#) + (C5# * Z2#))


					; Caster plane
					; Same as back plane, so we can reuse the plane normal ABC, which is normalized, and because
					; the normal is of length 1, we don't need to normalize D.
					A6# = -A5#
					B6# = -B5#
					C6# = -C5#
					D6# = -(A6#*Caster_X# + B6#*Caster_Y# + C6#*Caster_Z#)
												

					; These values will be needed to calculate the UV mapping for each vertex.
					; They only need to be calculated once per light/caster pair.
					E1x# = X2# - X1#
					E1y# = Y2# - Y1#
					E1z# = Z2# - Z1#

					E2x# = X3# - X1#
					E2y# = Y3# - Y1#
					E2z# = Z3# - Z1#

					Hx# = (Light_NY# * E2z#) - (E2y# * Light_NZ#)
					Hy# = (Light_NZ# * E2x#) - (E2z# * Light_NX#)
					Hz# = (Light_NX# * E2y#) - (E2x# * Light_NY#)

					A# = (E1x# * Hx#) + (E1y# * Hy#) + (E1z# * Hz#)

					F# = 1.0 / A#


			; If realtime rendered shadows are enabled for this caster, then render the shadow for this caster
			; from the viewpoint of this light source.
			If ThisCaster\RenderTexture = 1 


				; Get the orientation of the caster.
				Caster_Pitch# = EntityPitch#(ThisCaster\Entity, True)
				Caster_Yaw#   = EntityYaw#(ThisCaster\Entity, True)
				Caster_Roll#  = EntityRoll#(ThisCaster\Entity, True)

				; Place the copy of the shadow caster at the same location as the real entity.
				PositionEntity Caster_Pivot, Caster_X#, Caster_Y#, Caster_Z#, True


				; Rotate the copy of the shadow caster to the same orientation as the real entity.
				RotateEntity Caster_Pivot, Caster_Pitch#, Caster_Yaw#, Caster_Roll#, True
										
					
				; Point the light pivot towards the caster pivot.
				PointEntity Light_Pivot, Caster_Pivot
				

				; Make the shadow caster copy a child of the light source pivot.  
				; Retain it's global position / orientation
				EntityParent Caster_Pivot, Light_Pivot, True


				; Determine the diffrence in angle between the caster and light.
				Light_Relative_Caster_Pitch# = EntityPitch#(Caster_Pivot, False)
				Light_Relative_Caster_Yaw#   = EntityYaw#(Caster_Pivot, False)
				Light_Relative_Caster_Roll#  = EntityRoll#(Caster_Pivot, False)


				; Unparent the caster_pivot from the light source.
				EntityParent Caster_Pivot, 0, True
				
				
				; If the angle between light and caster has changed significantly enough, OR if the entity is
				; an animated mesh which is curently animating, render a new shadow texture.
				Render_Texture = False
				
				If Light_Relative_Caster_Pitch# < (ThisSet\Texture_Pitch#-Texture_Angle_Epsilon#) Or Light_Relative_Caster_Pitch# > (ThisSet\Texture_Pitch#+Texture_Angle_Epsilon#)
					Render_Texture = True
				Else
					If Light_Relative_Caster_Yaw# < (ThisSet\Texture_Yaw#-Texture_Angle_Epsilon#) Or Light_Relative_Caster_Yaw# > (ThisSet\Texture_Yaw#+Texture_Angle_Epsilon#)
						Render_Texture = True
					Else
						If Light_Relative_Caster_Roll# < (ThisSet\Texture_Roll#-Texture_Angle_Epsilon#) Or Light_Relative_Caster_Roll# > (ThisSet\Texture_Roll#+Texture_Angle_Epsilon#)
							Render_Texture = True
						EndIf
					EndIf
				EndIf
							
				
				If Render_Texture Or Animating(ThisCaster\Entity)


					ThisSet\Texture_Pitch# = Light_Relative_Caster_Pitch#
					ThisSet\Texture_Yaw#   = Light_Relative_Caster_Yaw#
					ThisSet\Texture_Roll#  = Light_Relative_Caster_Roll#										 


					; Create a copy of the caster which we can manipulate without affecting the original.
					Shadow_Caster = CopyEntity(ThisCaster\Entity)

					; Place the copy of the shadow caster at the same location as the real entity.
					PositionEntity Shadow_Caster, Caster_X#, Caster_Y#, Caster_Z#, True


					; Rotate the copy of the shadow caster to the same orientation as the real entity.
					RotateEntity Shadow_Caster, Caster_Pitch#, Caster_Yaw#, Caster_Roll#, True


					; Point the light pivot towards the caster pivot.
					PointEntity Light_Pivot, Shadow_Caster
										
					
					; Make the shadow caster copy a child of the light source pivot.  
					; Retain it's global position / orientation
					EntityParent Shadow_Caster, Light_Pivot, True
															
				
					; Rotate the light source pivot so that it points straight down just like the
					; camera which will be creating the texture.
					RotateEntity Light_Pivot, 90, 0, 0, True 
					
			
					; Detach the shadow caster copy from the light source.
					EntityParent Shadow_Caster, 0
									
			
					; Position the copy of the shadow caster at the position in the world where the
					; light source cna render it's shadow map.
					PositionEntity Shadow_Caster, 0, Shadow_Cam_Position# + Shadow_Caster_Offset#, 0, True

					
					; Scale the shadow caster so it's widest siloughette when not rotated fits inside the texture.
					; Object may become a bit wider than this when rotated, so we make sure to leave enough 
					; room for error in the shadow map.  Also scale it to match the relative scale on each axis as
					; the original caster.
					Scale_Factor# = 1.0 / ThisCaster\Radius#
					ScaleEntity Shadow_Caster, Scale_Factor#*Caster_Scale_X#, Scale_Factor#*Caster_Scale_Y#, Scale_Factor#*Caster_Scale_Z#


					; If the shadow is set to render normally...
					If ThisCaster\Translucent = 0

						; Make shadow caster a solid dark color.
						;EntityTexture Shadow_Caster, Shadow_Caster_Texture
						
						; Unaffected by fog, fullbright.
						EntityFX Shadow_Caster, 1+8

						; Make the shadow caster black.
						EntityColor Shadow_Caster, 0, 0, 0
						
					Else
					
						; This isn't quite up to snuff yet.  It works well enough, but I'd like to make it so that
						; the shadow gets darker as the object casting the shadow becomes more opaque.  I haven't
						; worked out yet how to accomplish that though since I can't get the color of the object
						; in Blitz, and I really can't make the user pass the color of the object to the shadow 
						; system every frame.
					
						; Fullbright, unaffected by fog, disable backface culling.
						EntityFX Shadow_Caster, 1+8+16

						; Set entity to be semi transparent.
						EntityAlpha Shadow_Caster, 0.8
												
					EndIf


					; Set the camera's viewpoint to be the same size as the texture we want to create.
					CameraViewport Shadow_Cam, 0, 0, ThisCaster\Resolution, ThisCaster\Resolution
	
					; Render the shadow view.
					RenderWorld

					; Copy the new shadow texture from the screen buffer to the texture buffer.		
					CopyRect 0, 0, ThisCaster\Resolution, ThisCaster\Resolution, 0, 0, BackBuffer(), TextureBuffer(ThisSet\Texture)


					; Delete the copy of the shadow caster we made.
					FreeEntity Shadow_Caster

					
				EndIf	

			EndIf



			; Loop through all shadow receivers.
			For ThisReceiver.Shadow_Receiver = Each Shadow_Receiver						


				; Make sure casters do not try to cast onto themselves!				
				If ThisReceiver\Entity <> ThisCaster\Entity 


					; Get info about the shadow recevier.
					Receiver_X# = EntityX#(ThisReceiver\Entity, True)
					Receiver_Y# = EntityY#(ThisReceiver\Entity, True) 
					Receiver_Z# = EntityZ#(ThisReceiver\Entity, True)

				
					; Check to see if this receiver is within the light's view frustrum at all.
					Receiver_In_Region = True

		
					; Is receiver above top plane?
					If (A1#*Receiver_X# + B1#*Receiver_Y# + C1#*Receiver_Z# + D1#) < -ThisReceiver\Radius#
						Receiver_In_Region = False
					Else
						
						; Is receiver below bottom plane?
						If (A2#*Receiver_X# + B2#*Receiver_Y# + C2#*Receiver_Z# + D2#) < -ThisReceiver\Radius#
							Receiver_In_Region = False
						Else	
																
							; Is receiver to left of left plane?
							If (A3#*Receiver_X# + B3#*Receiver_Y# + C3#*Receiver_Z# + D3#) < -ThisReceiver\Radius#
								Receiver_In_Region = False
							Else
															
								; Is receiver to right of right plane?
								If (A4#*Receiver_X# + B4#*Receiver_Y# + C4#*Receiver_Z# + D4#) < -ThisReceiver\Radius#
									Receiver_In_Region = False
								Else

									; Is receiver in front of caster?
									If (A6#*Receiver_X# + B6#*Receiver_Y# + C6#*Receiver_Z# + D6#) > ThisReceiver\Radius#
										Receiver_In_Region = False
									EndIf
									
								EndIf
							
							EndIf
							
						EndIf
						
					EndIf



					If Receiver_In_Region

						; Find the surface of this shadow mesh.
						SURFACE_Shadow = GetSurface(ThisShadow\Mesh, 1)

						; Loop through all triangles in all surfaces of the reciever.
						Surfaces = CountSurfaces(ThisReceiver\Entity)
						For LOOP_Surface = 1 To Surfaces

							Surface_Handle = GetSurface(ThisReceiver\Entity, LOOP_Surface)

							Tris = CountTriangles(Surface_Handle)
							For LOOP_Tris = 0 To Tris-1
									
								; Check to see if the triangle is inside the shadow's bounding frustum.
								;
								; This test works by seeing if all of a triangle's points are on a specific side of
								; each plane of the frustum.
								;
								; The test is not 100% accurate... a very few triangles will pass the test but
								; actually be outside the region.  This is true for triangles which stretch across
								; the frustrum corners just outside the region.
								;
								; But as we are concerned only with making sure we find ALL the triangles which ARE
								; in the region and cull MOST outside the region, a little sloppiness is okay...
								; we are more concerned with culling 10,000 polygons fast than having a couple extras
								; in the final shadow.

								Vertex_0 = TriangleVertex(Surface_Handle, LOOP_Tris, 0)
								VX0# = VertexX#(Surface_Handle, Vertex_0) + Receiver_X#
								VY0# = VertexY#(Surface_Handle, Vertex_0) + Receiver_Y#
								VZ0# = VertexZ#(Surface_Handle, Vertex_0) + Receiver_Z#

								Vertex_1 = TriangleVertex(Surface_Handle, LOOP_Tris, 1)
								VX1# = VertexX#(Surface_Handle, Vertex_1) + Receiver_X#
								VY1# = VertexY#(Surface_Handle, Vertex_1) + Receiver_Y#
								VZ1# = VertexZ#(Surface_Handle, Vertex_1) + Receiver_Z#

								Vertex_2 = TriangleVertex(Surface_Handle, LOOP_Tris, 2)
								VX2# = VertexX#(Surface_Handle, Vertex_2) + Receiver_X#
								VY2# = VertexY#(Surface_Handle, Vertex_2) + Receiver_Y#
								VZ2# = VertexZ#(Surface_Handle, Vertex_2) + Receiver_Z#


								Poly_In_Region = False

								; Is polygon below top plane?
								If (A1#*VX0# + B1#*VY0# + C1#*VZ0# + D1#) > 0
									Poly_In_Region = True
								Else
									If (A1#*VX1# + B1#*VY1# + C1#*VZ1# + D1#) > 0
										Poly_In_Region = True
									Else
										If (A1#*VX2# + B1#*VY2# + C1#*VZ2# + D1#) > 0
											Poly_In_Region = True
										EndIf
									EndIf
								EndIf

							
								; If the polygon is still a candidate for being in the region, do the next test.																	
								If Poly_In_Region

									Poly_In_Region = False

									; Is polygon above bottom plane?
									If (A2#*VX0# + B2#*VY0# + C2#*VZ0# + D2#) > 0
										Poly_In_Region = True
									Else
										If (A2#*VX1# + B2#*VY1# + C2#*VZ1# + D2#) > 0
											Poly_In_Region = True
										Else
											If (A2#*VX2# + B2#*VY2# + C2#*VZ2# + D2#) > 0
												Poly_In_Region = True
											EndIf
										EndIf
									EndIf
									
									
									If Poly_In_Region
	
										Poly_In_Region = False
	
										; Is polygon to right of left plane?
										If (A3#*VX0# + B3#*VY0# + C3#*VZ0# + D3#) > 0
											Poly_In_Region = True
										Else
											If (A3#*VX1# + B3#*VY1# + C3#*VZ1# + D3#) > 0
												Poly_In_Region = True
											Else
												If (A3#*VX2# + B3#*VY2# + C3#*VZ2# + D3#) > 0
													Poly_In_Region = True
												EndIf
											EndIf
										EndIf
									

										If Poly_In_Region
		
											Poly_In_Region = False
	
											; Is polygon to left of right plane?
											If (A4#*VX0# + B4#*VY0# + C4#*VZ0# + D4#) > 0
												Poly_In_Region = True
											Else
												If (A4#*VX1# + B4#*VY1# + C4#*VZ1# + D4#) > 0
													Poly_In_Region = True
												Else
													If (A4#*VX2# + B4#*VY2# + C4#*VZ2# + D4#) > 0
														Poly_In_Region = True
													EndIf
												EndIf
											EndIf
											

											If Poly_In_Region

												Poly_In_Region = False

												; Is polygon behind caster?
												If (A6#*VX0# + B6#*VY0# + C6#*VZ0# + D6#) < 0
													Poly_In_Region = True
												Else
													If (A6#*VX1# + B6#*VY1# + C6#*VZ1# + D6#) < 0
														Poly_In_Region = True
													Else
														If (A6#*VX2# + B6#*VY2# + C6#*VZ2# + D6#) < 0
															Poly_In_Region = True
														EndIf
													EndIf
												EndIf
																	
	
												If Poly_In_Region
																			

													; Get the normals of the vertices.
													NX0# = VertexNX#(Surface_Handle, Vertex_0)
													NX1# = VertexNX#(Surface_Handle, Vertex_1)
													NX2# = VertexNX#(Surface_Handle, Vertex_2)
	
													NY0# = VertexNY#(Surface_Handle, Vertex_0)
													NY1# = VertexNY#(Surface_Handle, Vertex_1)
													NY2# = VertexNY#(Surface_Handle, Vertex_2)
	
													NZ0# = VertexNZ#(Surface_Handle, Vertex_0)
													NZ1# = VertexNZ#(Surface_Handle, Vertex_1)
													NZ2# = VertexNZ#(Surface_Handle, Vertex_2)
	

													; Calculate the dot product of the vertex normal and the light normal.
													; This is the angle, -1..0..1, of the light source to the vertex.
													; If this value is negative, then the normal points away from the light source.
	
														; Vertex 0																																							
														V0_Angle# = (NX0# * -Light_NX#) + (NY0# * -Light_NY#) + (NZ0# * -Light_NZ#)

				 										; Vertex 1																																							
														V1_Angle# = (NX1# * -Light_NX#) + (NY1# * -Light_NY#) + (NZ1# * -Light_NZ#)

														; Vertex 2																																							
														V2_Angle# = (NX2# * -Light_NX#) + (NY2# * -Light_NY#) + (NZ2# * -Light_NZ#)
													

													; If any of the vertices are facing towards the light...
													If (V0_Angle# > 0) Or (V1_Angle# > 0) Or (V2_Angle# > 0)
	

														; Add this polygon to our shadow!
														Shadow_Poly_Count = Shadow_Poly_Count + 1

	
														; Calculate the UV coordinates of the vertices.
														; Small optimization note:
														; Replace the light normal later with the normal from the caster plane.
																	
															; Vertex 0
																Sx# = VX0# - X1#
																Sy# = VY0# - Y1#
																Sz# = VZ0# - Z1#
																V0u# = F# * ((Sx# * Hx#) + (Sy# * Hy#) + (Sz# * Hz#))

																Qx# = (Sy# * E1z#) - (E1y# * Sz#)
																Qy# = (Sz# * E1x#) - (E1z# * Sx#)
																Qz# = (Sx# * E1y#) - (E1x# * Sy#)
			
																V0v# = F# * ((Light_NX# * Qx#) + (Light_NY# * Qy#) + (Light_NZ# * Qz#))

		
															; Vertex 1
																Sx# = VX1# - X1#
																Sy# = VY1# - Y1#
																Sz# = VZ1# - Z1#
	
																V1u# = F# * ((Sx# * Hx#) + (Sy# * Hy#) + (Sz# * Hz#))
	
																Qx# = (Sy# * E1z#) - (E1y# * Sz#)
																Qy# = (Sz# * E1x#) - (E1z# * Sx#)
																Qz# = (Sx# * E1y#) - (E1x# * Sy#)
				
																V1v# = F# * ((Light_NX# * Qx#) + (Light_NY# * Qy#) + (Light_NZ# * Qz#))

	
															; Vertex 2
																Sx# = VX2# - X1#
																Sy# = VY2# - Y1#
																Sz# = VZ2# - Z1#
	
																V2u# = F# * ((Sx# * Hx#) + (Sy# * Hy#) + (Sz# * Hz#))

																Qx# = (Sy# * E1z#) - (E1y# * Sz#)
																Qy# = (Sz# * E1x#) - (E1z# * Sx#)
																Qz# = (Sx# * E1y#) - (E1x# * Sy#)
			
																V2v# = F# * ((Light_NX# * Qx#) + (Light_NY# * Qy#) + (Light_NZ# * Qz#))
			

														; Add the vertices for the new shadow triangle to the shadow mesh, while nudging the vertices
														; in the direction of the vertex normals so the shadow renders in front of the shadowed polygon.
														Shadow_Vertex_0 = AddVertex(SURFACE_Shadow, VX0#+(NX0#*Shadow_Nudge#), VY0#+(NY0#*Shadow_Nudge#), VZ0#+(NZ0#*Shadow_Nudge#), V0u#, V0v#)
														Shadow_Vertex_1 = AddVertex(SURFACE_Shadow, VX1#+(NX1#*Shadow_Nudge#), VY1#+(NY1#*Shadow_Nudge#), VZ1#+(NZ1#*Shadow_Nudge#), V1u#, V1v#)
														Shadow_Vertex_2 = AddVertex(SURFACE_Shadow, VX2#+(NX2#*Shadow_Nudge#), VY2#+(NY2#*Shadow_Nudge#), VZ2#+(NZ2#*Shadow_Nudge#), V2u#, V2v#)
															
																
														; Adjust shadow brightness at each vertex according to how much the
														; vertex normal points away from the light source, using lambert shading.
														
															; Multiply the angle by the color of the model.
															; In our case we want white, so we can calculate just one value.
															; 256^2 = 65536
															VC = -V0_Angle# * 65536.0
															If VC < 0 Then VC = 0
															VertexColor SURFACE_Shadow, Shadow_Vertex_0, VC+(Shadow_Ambient_Light_R*Shadow_Ambient_Multiplier#), VC+(Shadow_Ambient_Light_G*Shadow_Ambient_Multiplier#), VC+(Shadow_Ambient_Light_B*Shadow_Ambient_Multiplier#)
															
															; Repeat for vertices 2 and 3.
															VC = -V1_Angle# * 65536.0
															If VC < 0 Then VC = 0
															VertexColor SURFACE_Shadow, Shadow_Vertex_1, VC+(Shadow_Ambient_Light_R*Shadow_Ambient_Multiplier#), VC+(Shadow_Ambient_Light_G*Shadow_Ambient_Multiplier#), VC+(Shadow_Ambient_Light_B*Shadow_Ambient_Multiplier#)

															VC = -V2_Angle# * 65536.0
															If VC < 0 Then VC = 0
															VertexColor SURFACE_Shadow, Shadow_Vertex_2, VC+(Shadow_Ambient_Light_R*Shadow_Ambient_Multiplier#), VC+(Shadow_Ambient_Light_G*Shadow_Ambient_Multiplier#), VC+(Shadow_Ambient_Light_B*Shadow_Ambient_Multiplier#)
									
						
														; Add the triangle to the shadow mesh.
														AddTriangle(SURFACE_Shadow, Shadow_Vertex_0, Shadow_Vertex_1, Shadow_Vertex_2)

													EndIf

												EndIf
	
											EndIf
	
										EndIf
										
									EndIf		

								EndIf

							Next	; Next Tri			
		
						Next	; Next Surface


						; Set new shadow mesh to be full bright, use vertex colors, no fog.
						EntityFX ThisShadow\Mesh, 1+2+8

	
						; Multiply blend shadow mesh with stuff behind it.
						EntityBlend ThisShadow\Mesh, 2 


						; Texture the shadow mesh with the appropriate texture depending on the type of shadow it is.
						If ThisCaster\RenderTexture = 1 
							EntityTexture ThisShadow\Mesh, ThisSet\Texture
						Else
							EntityTexture ThisShadow\Mesh, ThisCaster\Texture
						EndIf	


						; Advance pointer to the next shadow mesh.
						ThisShadow = After ThisShadow

	
					EndIf 	; If receiver is in shadow volume...

				EndIf	; If receiver <> caster

			Next	; Next receiver.

		EndIf

	Next	; Next set.


	;FreeTexture Shadow_Caster_Texture
	FreeEntity Shadow_Cam
	FreeEntity Light_Pivot	
	FreeEntity Caster_Pivot

	ShowEntity Current_Camera
	;CameraProjMode Current_Camera, 1
		
End Function


; -------------------------------------------------------------------------------------------------------------------
; This function deletes all shadow casters which reference the specified entity.
;
; When you delete an entity which is casting a shadow, you must call this function so that the game
; does not crash from trying to reference an entity which does not exist.
; -------------------------------------------------------------------------------------------------------------------
Function Delete_Shadow_Caster(Entity)


	; Delete all caster sets which reference this caster, and free their associated textures.
	For ThisSet.Caster_Set = Each Caster_Set

		If ThisSet\Caster\Entity = Entity
	
			; If this set was casting rendered shadows, free the texture.	
			If ThisSet\Texture <> 0 Then FreeTexture ThisSet\Texture
		
			Delete ThisSet
			
		EndIf

	Next


	For ThisCaster.Shadow_Caster = Each Shadow_Caster

		If (ThisCaster\Entity = Entity) 


			; Delete one shadow mesh for each recevier/light pair.
			For Loop.Shadow_Receiver = Each Shadow_Receiver
	
				For Loop2.Light_Caster = Each Light_Caster

					ThisShadow.Shadow = Last Shadow
					FreeEntity ThisShadow\Mesh
					Delete ThisShadow 
	
				Next
			
			Next	

			; Delete the data about the caster.
			Delete ThisCaster

		EndIf	

	Next
	

End Function


; -------------------------------------------------------------------------------------------------------------------
; This function deletes all shadow receivers which reference the specified entity.
;
; When you delete an entity which is receiving a shadow, you must call this function so that the game
; does not crash from trying to reference an entity which does not exist.
; -------------------------------------------------------------------------------------------------------------------
Function Delete_Shadow_Receiver(Entity)

	For ThisReceiver.Shadow_Receiver = Each Shadow_Receiver

		If (ThisReceiver\Entity = Entity)

			; Delete one shadow mesh for each caster/light pair.
			For Loop.Shadow_Caster = Each Shadow_Caster
	
				For Loop2.Light_Caster = Each Light_Caster

					ThisShadow.Shadow = Last Shadow
					FreeEntity ThisShadow\Mesh
					Delete ThisShadow 
	
				Next
			
			Next	


			; Delete the data about the receiver
			Delete ThisReceiver

		EndIf	

	Next	


End Function


; -------------------------------------------------------------------------------------------------------------------
; This function deletes all light casters which reference the specified light.
;
; When you delete a light which is casting shadows, you must call this function so that the game
; does not crash from trying to reference an entity which does not exist.
; -------------------------------------------------------------------------------------------------------------------
Function Delete_Light_Caster(Entity)


	; Delete all caster sets which reference this caster, and free their associated textures.
	For ThisSet.Caster_Set = Each Caster_Set

		If ThisSet\Light\Entity = Entity
	
			; If this set was casting rendered shadows, free the texture.	
			If ThisSet\Texture <> 0 Then FreeTexture ThisSet\Texture
		
			Delete ThisSet
			
		EndIf

	Next


	For ThisCaster.Light_Caster = Each Light_Caster

		If (ThisCaster\Entity = Entity)

			; Delete one shadow mesh for each recevier/caster pair.
			For Loop.Shadow_Receiver = Each Shadow_Receiver
	
				For Loop2.Shadow_Caster = Each Shadow_Caster

					ThisShadow.Shadow = Last Shadow
					FreeEntity ThisShadow\Mesh
					Delete ThisShadow 
	
				Next
			
			Next	


			; Delete the data about the caster.
			Delete ThisCaster
		
		EndIf	

	Next	


End Function


; -------------------------------------------------------------------------------------------------------------------
; This function deletes all old static shadows and recreates a new shadow mesh for each static shadow casting object.
;
; You should call this once after you place your light sources and define which objects you want to cast static
; shadows.  Static shadows will not change if you move the lights in your world unless you call this function again.
;
; Current_Camera = The camera viewing the scene.  Neccessary so we can hide it while we render the shadow textures.
; -------------------------------------------------------------------------------------------------------------------
Function Update_Static_Shadows(Current_Camera)


	; Turn off the main camera view.
		HideEntity Current_Camera


	; Reset the count of the number of polys in all the shadows combined.
		Shadow_Poly_Count = 0


	; Make a blank texture we can use to un-texture the shadow meshes with.
		;Shadow_Caster_Texture = CreateTexture(32, 32, 0)
		;TextureBlend Shadow_Caster_Texture, 0


	; Create a camera to render the shadows with.
	
		; Create a new camera.
		Shadow_Cam = CreateCamera()
		Shadow_Cam_Position#  = -65535.0
		Shadow_Caster_Offset# = -16.0

		; Set the camera's range to be very small so as to reduce the possiblity of extra objects making it into the scene.
		CameraRange Shadow_Cam, 0.1, 100

		; Set the screen clear mode, and set the clear color to white.
		CameraClsMode Shadow_Cam, True, True
		CameraClsColor Shadow_Cam, 255, 255, 255
		
		; Set the camera to zoom in on the object to reduce perspective error from the object being too close to the camera.
		CameraZoom Shadow_Cam, 16.0
				
		; Aim camera straight down.	
		RotateEntity Shadow_Cam, 90, 0, 0, True

		; Position camera very far down in world so as to probably not have any other objects visible.
		PositionEntity Shadow_Cam, 0, Shadow_Cam_Position#, 0, True


	; Create a pivot to represent the light source.
		Light_Pivot = CreatePivot()


	; Delete all the static shadow meshes.
	For ThisShadow.Static_Shadow = Each Static_Shadow
		FreeEntity ThisShadow\Mesh
	Next


	; Delete all the static shadow textures.
	For ThisTexture.Static_Shadow_Texture = Each Static_Shadow_Texture
		FreeTexture ThisTexture\Texture
	Next 	

		
	; Loop through all the pairs of light sources.
	For ThisLight.Light_Caster = Each Light_Caster


		; Get the location of the light in world space.
		Light_X# = EntityX#(ThisLight\Entity, True)
		Light_Y# = EntityY#(ThisLight\Entity, True)
		Light_Z# = EntityZ#(ThisLight\Entity, True)


		; Loop through all the shadow casters.
		For ThisCaster.Static_Shadow_Caster = Each Static_Shadow_Caster


			; Get the location of the shadow caster in world space.	
			Caster_X# = EntityX#(ThisCaster\Entity, True)
			Caster_Y# = EntityY#(ThisCaster\Entity, True)
			Caster_Z# = EntityZ#(ThisCaster\Entity, True)


			; Calculate the distance between the light and the caster.
			Light_To_Caster_Distance# = Sqr((Light_X#-Caster_X#)*(Light_X#-Caster_X#) + (Light_Y#-Caster_Y#)*(Light_Y#-Caster_Y#) + (Light_Z#-Caster_Z#)*(Light_Z#-Caster_Z#))


			; Is this shadow caster near enough to the light source to cast a shadow at all?
			If Light_To_Caster_Distance# <= ThisLight\Range#


				; Position the light pivot where the light is in the world.
				PositionEntity Light_Pivot, Light_X#, Light_Y#, Light_Z#, True
				

				; Point light_pivot at the caster.								
				PointEntity Light_Pivot, ThisCaster\Entity


				; Calculate the normal of the light's ray in world space.
				; We use this normal to calculate how to project the points in the world onto the light's plane.
				;
				; Optimization note: 
				; The normal of the caster plane will be the same as this normal, so we will not need to calculate
				; this normal.
				; (A, B, C) will be the normal of the plane.
				TFormNormal 0, 0, 1, Light_Pivot, 0
				Light_NX# = TFormedX#()
				Light_NY# = TFormedY#()
				Light_NZ# = TFormedZ#()


				; Calculate the plane equations for the 4 planes which make up this light's view frustrum.

					; Calculate the locations, in world space, of six points around the light which we
					; can use to define the four planes for the view frustrum.

						; Top left.
						TFormPoint -ThisCaster\Radius#, ThisCaster\Radius#, 0, Light_Pivot, 0
						X1# = TFormedX#()
						Y1# = TFormedY#()
						Z1# = TFormedZ#()
					
						; Top Right
						TFormPoint ThisCaster\Radius#, ThisCaster\Radius#, 0, Light_Pivot, 0
						X2# = TFormedX#()
						Y2# = TFormedY#()
						Z2# = TFormedZ#()

						; Bottom Left 
						TFormPoint -ThisCaster\Radius#, -ThisCaster\Radius#, 0, Light_Pivot, 0		
						X3# = TFormedX#()
						Y3# = TFormedY#()
						Z3# = TFormedZ#()

						; Top Left Forward
						TFormPoint -ThisCaster\Radius#, ThisCaster\Radius#, 1, Light_Pivot, 0									
						X4# = TFormedX#()
						Y4# = TFormedY#()
						Z4# = TFormedZ#()

						; Bottom Right
						TFormPoint ThisCaster\Radius#, -ThisCaster\Radius#, 0, Light_Pivot, 0
						X5# = TFormedX#()
						Y5# = TFormedY#()
						Z5# = TFormedZ#()

						; Bottom
						TFormPoint 0, -ThisCaster\Radius#, 0, Light_Pivot, 0
						X6# = TFormedX#()
						Y6# = TFormedY#()
						Z6# = TFormedZ#()
					
						; Right
						TFormPoint ThisCaster\Radius#, 0, 0, Light_Pivot, 0
						X7# = TFormedX#()
						Y7# = TFormedY#()
						Z7# = TFormedZ#()

						; Bottom Right Forward
						TFormPoint ThisCaster\Radius#, -ThisCaster\Radius#, 1, Light_Pivot, 0
						X8# = TFormedX#()
						Y8# = TFormedY#()
						Z8# = TFormedZ#()
					
				
					; Calculate the plane equations for each one of the faces.		
					; Points provided to the plane in counterclockwise order so the normal points up.

						; Top plane
						; (4, 1, 2)
									
						Ax# = X1#-X4#
						Ay# = Y1#-Y4#
						Az# = Z1#-Z4#

						Bx# = X2#-X4#
						By# = Y2#-Y4#
						Bz# = Z2#-Z4#

						Nx# = (Ay# * Bz#) - (By# * Az#)
						Ny# = (Az# * Bx#) - (Bz# * Ax#) 	
						Nz# = (Ax# * By#) - (Bx# * Ay#)
	
						Nl# = Sqr(Nx#*Nx# + Ny#*Ny# + Nz#*Nz#)
		
						A1# = Nx# / Nl#
						B1# = Ny# / Nl#
						C1# = Nz# / Nl#
	
						D1# = -((A1# * X4#) + (B1# * Y4#) + (C1# * Z4#))


						; Bottom plane
						; (8, 5, 6)

						Ax# = X5#-X8#
						Ay# = Y5#-Y8#
						Az# = Z5#-Z8#
	
						Bx# = X6#-X8#
						By# = Y6#-Y8#
						Bz# = Z6#-Z8#

						Nx# = (Ay# * Bz#) - (By# * Az#)
						Ny# = (Az# * Bx#) - (Bz# * Ax#) 	
						Nz# = (Ax# * By#) - (Bx# * Ay#)
	
						Nl# = Sqr(Nx#*Nx# + Ny#*Ny# + Nz#*Nz#)
	
						A2# = Nx# / Nl#
						B2# = Ny# / Nl#
						C2# = Nz# / Nl#
	
						D2# = -((A2# * X8#) + (B2# * Y8#) + (C2# * Z8#))

									
						; Left plane
						; (1, 4, 3)
					
						Ax# = X4#-X1#
						Ay# = Y4#-Y1#
						Az# = Z4#-Z1#

						Bx# = X3#-X1#
						By# = Y3#-Y1#
						Bz# = Z3#-Z1#

						Nx# = (Ay# * Bz#) - (By# * Az#)
						Ny# = (Az# * Bx#) - (Bz# * Ax#) 	
						Nz# = (Ax# * By#) - (Bx# * Ay#)
	
						Nl# = Sqr(Nx#*Nx# + Ny#*Ny# + Nz#*Nz#)
	
						A3# = Nx# / Nl#
						B3# = Ny# / Nl#
						C3# = Nz# / Nl#
	
						D3# = -((A3# * X1#) + (B3# * Y1#) + (C3# * Z1#))


						; Right plane
						; (7, 5, 8)

						Ax# = X5#-X7#
						Ay# = Y5#-Y7#
						Az# = Z5#-Z7#

						Bx# = X8#-X7#
						By# = Y8#-Y7#
						Bz# = Z8#-Z7#

						Nx# = (Ay# * Bz#) - (By# * Az#)
						Ny# = (Az# * Bx#) - (Bz# * Ax#) 	
						Nz# = (Ax# * By#) - (Bx# * Ay#)
	
						Nl# = Sqr(Nx#*Nx# + Ny#*Ny# + Nz#*Nz#)
	
						A4# = Nx# / Nl#
						B4# = Ny# / Nl#
						C4# = Nz# / Nl#
	
						D4# = -((A4# * X7#) + (B4# * Y7#) + (C4# * Z7#))


						; Back plane
						; (2, 1, 3)

						Ax# = X1#-X2#
						Ay# = Y1#-Y2#
						Az# = Z1#-Z2#

						Bx# = X3#-X2#
						By# = Y3#-Y2#
						Bz# = Z3#-Z2#

						Nx# = (Ay# * Bz#) - (By# * Az#)
						Ny# = (Az# * Bx#) - (Bz# * Ax#) 	
						Nz# = (Ax# * By#) - (Bx# * Ay#)
	
						Nl# = Sqr(Nx#*Nx# + Ny#*Ny# + Nz#*Nz#)
		
						A5# = Nx# / Nl#
						B5# = Ny# / Nl#
						C5# = Nz# / Nl#
	
						D5# = -((A5# * X2#) + (B5# * Y2#) + (C5# * Z2#))


						; Caster plane
						; Same as back plane, so we can reuse the plane normal ABC, which is normalized, and because
						; the normal is of length 1, we don't need to normalize D.
						A6# = -A5#
						B6# = -B5#
						C6# = -C5#
						D6# = -(A6#*Caster_X# + B6#*Caster_Y# + C6#*Caster_Z#)
												

						; These values will be needed to calculate the UV mapping for each vertex.
						; They only need to be calculated once per light/caster pair.
						E1x# = X2# - X1#
						E1y# = Y2# - Y1#
						E1z# = Z2# - Z1#

						E2x# = X3# - X1#
						E2y# = Y3# - Y1#
						E2z# = Z3# - Z1#
	
						Hx# = (Light_NY# * E2z#) - (E2y# * Light_NZ#)
						Hy# = (Light_NZ# * E2x#) - (E2z# * Light_NX#)
						Hz# = (Light_NX# * E2y#) - (E2x# * Light_NY#)
	
						A# = (E1x# * Hx#) + (E1y# * Hy#) + (E1z# * Hz#)
	
						F# = 1.0 / A#


				; Render the shadow for this caster from the viewpoint of this light source.


					; Get the orientation of the caster.
					Caster_Pitch# = EntityPitch#(ThisCaster\Entity, True)
					Caster_Yaw#   = EntityYaw#(ThisCaster\Entity, True)
					Caster_Roll#  = EntityRoll#(ThisCaster\Entity, True)


					; Create a copy of the caster which we can manipulate without affecting the original.
					Shadow_Caster = CopyEntity(ThisCaster\Entity)


					; Place the copy of the shadow caster at the same location as the real entity.
					PositionEntity Shadow_Caster, Caster_X#, Caster_Y#, Caster_Z#, True


					; Rotate the copy of the shadow caster to the same orientation as the real entity.
					RotateEntity Shadow_Caster, Caster_Pitch#, Caster_Yaw#, Caster_Roll#, True


					; Point the light pivot towards the caster pivot.
					PointEntity Light_Pivot, Shadow_Caster
										
					
					; Make the shadow caster copy a child of the light source pivot.  
					; Retain it's global position / orientation
					EntityParent Shadow_Caster, Light_Pivot, True
															
				
					; Rotate the light source pivot so that it points straight down just like the
					; camera which will be creating the texture.
					RotateEntity Light_Pivot, 90, 0, 0, True 
					
			
					; Detach the shadow caster copy from the light source.
					EntityParent Shadow_Caster, 0, True
									
			
					; Position the copy of the shadow caster at the position in the world where the
					; light source can render it's shadow map.
					PositionEntity Shadow_Caster, 0, Shadow_Cam_Position# + Shadow_Caster_Offset#, 0, True

					
					; Scale the shadow caster so it's widest siloughette when not rotated fits inside the texture.
					; Object may become a bit wider than this when rotated, so we make sure to leave enough 
					; room for error in the shadow map.
					Scale_Factor# = 1.0 / ThisCaster\Radius#
					ScaleEntity Shadow_Caster, Scale_Factor#, Scale_Factor#, Scale_Factor#


					; Make shadow caster a solid dark color.
					;EntityTexture Shadow_Caster, Shadow_Caster_Texture
		
						
					; Unaffected by fog, fullbright.
					EntityFX Shadow_Caster, 1+8


					; Make the shadow caster black.
					EntityColor Shadow_Caster, 0, 0, 0


					; Set the camera's viewpoint to be the same size as the texture we want to create.
					CameraViewport Shadow_Cam, 0, 0, ThisCaster\Resolution, ThisCaster\Resolution
	

					; Render the shadow view.
					RenderWorld


					; Create a UV clamped and unmanaged texture for this shadow.
					ThisTexture.Static_Shadow_Texture = New Static_Shadow_Texture
					ThisTexture\Texture = CreateTexture(ThisCaster\Resolution, ThisCaster\Resolution, 16+32+256)
					ThisTexture\Casting_Entity = ThisCaster\Entity

					; Set the texture blend mode to add, which is the mode we need so the vertex colors can fade the shadow.
					TextureBlend ThisTexture\Texture, 3


					; Copy the new shadow texture from the screen buffer to the texture buffer.		
					CopyRect 0, 0, ThisCaster\Resolution, ThisCaster\Resolution, 0, 0, BackBuffer(), TextureBuffer(ThisTexture\Texture)


					; Delete the copy of the shadow caster we made.
					FreeEntity Shadow_Caster



				; Loop through all shadow receivers.
				For ThisReceiver.Shadow_Receiver = Each Shadow_Receiver						


					; Make sure casters do not try to cast onto themselves!				
					If ThisReceiver\Entity <> ThisCaster\Entity 


						; Get info about the shadow recevier.
						Receiver_X# = EntityX#(ThisReceiver\Entity, True)
						Receiver_Y# = EntityY#(ThisReceiver\Entity, True) 
						Receiver_Z# = EntityZ#(ThisReceiver\Entity, True)

				
						; Check to see if this receiver is within the light's view frustrum at all.
						Receiver_In_Region = True

		
						; Is receiver above top plane?
						If (A1#*Receiver_X# + B1#*Receiver_Y# + C1#*Receiver_Z# + D1#) < -ThisReceiver\Radius#
							Receiver_In_Region = False
						Else
						
							; Is receiver below bottom plane?
							If (A2#*Receiver_X# + B2#*Receiver_Y# + C2#*Receiver_Z# + D2#) < -ThisReceiver\Radius#
								Receiver_In_Region = False
							Else	
																
								; Is receiver to left of left plane?
								If (A3#*Receiver_X# + B3#*Receiver_Y# + C3#*Receiver_Z# + D3#) < -ThisReceiver\Radius#
									Receiver_In_Region = False
								Else
															
									; Is receiver to right of right plane?
									If (A4#*Receiver_X# + B4#*Receiver_Y# + C4#*Receiver_Z# + D4#) < -ThisReceiver\Radius#
										Receiver_In_Region = False
									Else

										; Is receiver in front of caster?
										If (A6#*Receiver_X# + B6#*Receiver_Y# + C6#*Receiver_Z# + D6#) > ThisReceiver\Radius#
											Receiver_In_Region = False
										EndIf
									
									EndIf
							
								EndIf
							
							EndIf
						
						EndIf



						If Receiver_In_Region


							; Create a mesh for this static shadow.
							ThisShadow = New Static_Shadow
							ThisShadow\Mesh = CreateMesh()
							ThisShadow\Casting_Entity = ThisCaster\Entity

							
							; Attach the shadow mesh to the receiver so that you can move the receiver and the shadow
							; will move with it.
							; EntityParent ThisShadow\Mesh, ThisReceiver\Entity, True

							
							; Create a surface for this static shadow's mesh.
							SURFACE_Shadow = CreateSurface(ThisShadow\Mesh)


							; Loop through all triangles in all surfaces of the reciever.
							Surfaces = CountSurfaces(ThisReceiver\Entity)
							For LOOP_Surface = 1 To Surfaces

								Surface_Handle = GetSurface(ThisReceiver\Entity, LOOP_Surface)

								Tris = CountTriangles(Surface_Handle) 
								For LOOP_Tris = 0 To Tris-1

									
									; Check to see if the triangle is inside the shadow's bounding frustum.
									;
									; This test works by seeing if all of a triangle's points are on a specific side of
									; each plane of the frustum.
									;
									; The test is not 100% accurate... a very few triangles will pass the test but
									; actually be outside the region.  This is true for triangles which stretch across
									; the frustrum corners just outside the region.
									;
									; But as we are concerned only with making sure we find ALL the triangles which ARE
									; in the region and cull MOST outside the region, a little sloppiness is okay...
									; we are more concerned with culling 10,000 polygons fast than having a couple extras
									; in the final shadow.


									Vertex_0 = TriangleVertex(Surface_Handle, LOOP_Tris, 0)
									VX0# = VertexX#(Surface_Handle, Vertex_0) + Receiver_X#
									VY0# = VertexY#(Surface_Handle, Vertex_0) + Receiver_Y#
									VZ0# = VertexZ#(Surface_Handle, Vertex_0) + Receiver_Z#

									Vertex_1 = TriangleVertex(Surface_Handle, LOOP_Tris, 1)
									VX1# = VertexX#(Surface_Handle, Vertex_1) + Receiver_X#
									VY1# = VertexY#(Surface_Handle, Vertex_1) + Receiver_Y#
									VZ1# = VertexZ#(Surface_Handle, Vertex_1) + Receiver_Z#

									Vertex_2 = TriangleVertex(Surface_Handle, LOOP_Tris, 2)
									VX2# = VertexX#(Surface_Handle, Vertex_2) + Receiver_X#
									VY2# = VertexY#(Surface_Handle, Vertex_2) + Receiver_Y#
									VZ2# = VertexZ#(Surface_Handle, Vertex_2) + Receiver_Z#


									Poly_In_Region = False

									; Is polygon below top plane?
									If (A1#*VX0# + B1#*VY0# + C1#*VZ0# + D1#) > 0
										Poly_In_Region = True
									Else
										If (A1#*VX1# + B1#*VY1# + C1#*VZ1# + D1#) > 0
											Poly_In_Region = True
										Else
											If (A1#*VX2# + B1#*VY2# + C1#*VZ2# + D1#) > 0
												Poly_In_Region = True
											EndIf
										EndIf
									EndIf

							
									; If the polygon is still a candidate for being in the region, do the next test.																	
									If Poly_In_Region

										Poly_In_Region = False

										; Is polygon above bottom plane?
										If (A2#*VX0# + B2#*VY0# + C2#*VZ0# + D2#) > 0
											Poly_In_Region = True
										Else
											If (A2#*VX1# + B2#*VY1# + C2#*VZ1# + D2#) > 0
												Poly_In_Region = True
											Else
												If (A2#*VX2# + B2#*VY2# + C2#*VZ2# + D2#) > 0
													Poly_In_Region = True
												EndIf
											EndIf
										EndIf
									
									
										If Poly_In_Region
		
											Poly_In_Region = False
	
											; Is polygon to right of left plane?
											If (A3#*VX0# + B3#*VY0# + C3#*VZ0# + D3#) > 0
												Poly_In_Region = True
											Else
												If (A3#*VX1# + B3#*VY1# + C3#*VZ1# + D3#) > 0
													Poly_In_Region = True
												Else
													If (A3#*VX2# + B3#*VY2# + C3#*VZ2# + D3#) > 0
														Poly_In_Region = True
													EndIf
												EndIf
											EndIf
									

											If Poly_In_Region
		
												Poly_In_Region = False
	
												; Is polygon to left of right plane?
												If (A4#*VX0# + B4#*VY0# + C4#*VZ0# + D4#) > 0
													Poly_In_Region = True
												Else
													If (A4#*VX1# + B4#*VY1# + C4#*VZ1# + D4#) > 0
														Poly_In_Region = True
													Else
														If (A4#*VX2# + B4#*VY2# + C4#*VZ2# + D4#) > 0
															Poly_In_Region = True
														EndIf
													EndIf
												EndIf
											

												If Poly_In_Region

													Poly_In_Region = False

													; Is polygon behind caster?
													If (A6#*VX0# + B6#*VY0# + C6#*VZ0# + D6#) < 0
														Poly_In_Region = True
													Else
														If (A6#*VX1# + B6#*VY1# + C6#*VZ1# + D6#) < 0
															Poly_In_Region = True
														Else
															If (A6#*VX2# + B6#*VY2# + C6#*VZ2# + D6#) < 0
																Poly_In_Region = True
															EndIf
														EndIf
													EndIf
																	
	
													If Poly_In_Region
																			

														; Get the normals of the vertices.
														NX0# = VertexNX#(Surface_Handle, Vertex_0)
														NX1# = VertexNX#(Surface_Handle, Vertex_1)
														NX2# = VertexNX#(Surface_Handle, Vertex_2)
		
														NY0# = VertexNY#(Surface_Handle, Vertex_0)
														NY1# = VertexNY#(Surface_Handle, Vertex_1)
														NY2# = VertexNY#(Surface_Handle, Vertex_2)
	
														NZ0# = VertexNZ#(Surface_Handle, Vertex_0)
														NZ1# = VertexNZ#(Surface_Handle, Vertex_1)
														NZ2# = VertexNZ#(Surface_Handle, Vertex_2)
	

														; Calculate the dot product of the vertex normal and the light normal.
														; This is the angle, -1..0..1, of the light source to the vertex.
														; If this value is negative, then the normal points away from the light source.
	
															; Vertex 0																																							
															V0_Angle# = (NX0# * -Light_NX#) + (NY0# * -Light_NY#) + (NZ0# * -Light_NZ#)

				 											; Vertex 1																																							
															V1_Angle# = (NX1# * -Light_NX#) + (NY1# * -Light_NY#) + (NZ1# * -Light_NZ#)

															; Vertex 2																																							
															V2_Angle# = (NX2# * -Light_NX#) + (NY2# * -Light_NY#) + (NZ2# * -Light_NZ#)
	

														; If any of the vertices are facing towards the light...
														If (V0_Angle# > 0) Or (V1_Angle# > 0) Or (V2_Angle# > 0)
		

															; Add this polygon to our shadow!
															Shadow_Poly_Count = Shadow_Poly_Count + 1
	
	
															; Calculate the UV coordinates of the vertices.
															; Small optimization note:
															; Replace the light normal later with the normal from the caster plane.
																		
																; Vertex 0
																	Sx# = VX0# - X1#
																	Sy# = VY0# - Y1#
																	Sz# = VZ0# - Z1#
																	V0u# = F# * ((Sx# * Hx#) + (Sy# * Hy#) + (Sz# * Hz#))

																	Qx# = (Sy# * E1z#) - (E1y# * Sz#)
																	Qy# = (Sz# * E1x#) - (E1z# * Sx#)
																	Qz# = (Sx# * E1y#) - (E1x# * Sy#)
			
																	V0v# = F# * ((Light_NX# * Qx#) + (Light_NY# * Qy#) + (Light_NZ# * Qz#))

		
																; Vertex 1
																	Sx# = VX1# - X1#
																	Sy# = VY1# - Y1#
																	Sz# = VZ1# - Z1#
		
																	V1u# = F# * ((Sx# * Hx#) + (Sy# * Hy#) + (Sz# * Hz#))
		
																	Qx# = (Sy# * E1z#) - (E1y# * Sz#)
																	Qy# = (Sz# * E1x#) - (E1z# * Sx#)
																	Qz# = (Sx# * E1y#) - (E1x# * Sy#)
					
																	V1v# = F# * ((Light_NX# * Qx#) + (Light_NY# * Qy#) + (Light_NZ# * Qz#))

		
																; Vertex 2
																	Sx# = VX2# - X1#
																	Sy# = VY2# - Y1#
																	Sz# = VZ2# - Z1#
	
																	V2u# = F# * ((Sx# * Hx#) + (Sy# * Hy#) + (Sz# * Hz#))
	
																	Qx# = (Sy# * E1z#) - (E1y# * Sz#)
																	Qy# = (Sz# * E1x#) - (E1z# * Sx#)
																	Qz# = (Sx# * E1y#) - (E1x# * Sy#)
				
																	V2v# = F# * ((Light_NX# * Qx#) + (Light_NY# * Qy#) + (Light_NZ# * Qz#))
			

															; Add the vertices for the new shadow triangle to the shadow mesh, while nudging the vertices
															; in the direction of the vertex normals so the shadow renders in front of the shadowed polygon.
															Shadow_Vertex_0 = AddVertex(SURFACE_Shadow, VX0#+(NX0#*Shadow_Nudge#), VY0#+(NY0#*Shadow_Nudge#), VZ0#+(NZ0#*Shadow_Nudge#), V0u#, V0v#)
															Shadow_Vertex_1 = AddVertex(SURFACE_Shadow, VX1#+(NX1#*Shadow_Nudge#), VY1#+(NY1#*Shadow_Nudge#), VZ1#+(NZ1#*Shadow_Nudge#), V1u#, V1v#)
															Shadow_Vertex_2 = AddVertex(SURFACE_Shadow, VX2#+(NX2#*Shadow_Nudge#), VY2#+(NY2#*Shadow_Nudge#), VZ2#+(NZ2#*Shadow_Nudge#), V2u#, V2v#)
															
																
															; Adjust shadow brightness at each vertex according to how much the
															; vertex normal points away from the light source, using lambert shading.
														
																; Multiply the angle by the color of the model.
																; In our case we want white, so we can calculate just one value.
																; 256^2 = 65536
																VC = -V0_Angle# * 65536.0
																If VC < 0 Then VC = 0
																VertexColor SURFACE_Shadow, Shadow_Vertex_0, VC+(Shadow_Ambient_Light_R*Shadow_Ambient_Multiplier#), VC+(Shadow_Ambient_Light_G*Shadow_Ambient_Multiplier#), VC+(Shadow_Ambient_Light_B*Shadow_Ambient_Multiplier#)
															
																; Repeat for vertices 2 and 3.
																VC = -V1_Angle# * 65536.0
																If VC < 0 Then VC = 0
																VertexColor SURFACE_Shadow, Shadow_Vertex_1, VC+(Shadow_Ambient_Light_R*Shadow_Ambient_Multiplier#), VC+(Shadow_Ambient_Light_G*Shadow_Ambient_Multiplier#), VC+(Shadow_Ambient_Light_B*Shadow_Ambient_Multiplier#)

																VC = -V2_Angle# * 65536.0
																If VC < 0 Then VC = 0
																VertexColor SURFACE_Shadow, Shadow_Vertex_2, VC+(Shadow_Ambient_Light_R*Shadow_Ambient_Multiplier#), VC+(Shadow_Ambient_Light_G*Shadow_Ambient_Multiplier#), VC+(Shadow_Ambient_Light_B*Shadow_Ambient_Multiplier#)
									
						
															; Add the triangle to the shadow mesh.
															AddTriangle(SURFACE_Shadow, Shadow_Vertex_0, Shadow_Vertex_1, Shadow_Vertex_2)

														EndIf
	
													EndIf
	
												EndIf
	
											EndIf
											
										EndIf		

									EndIf

								Next	; Next Tri			
				
							Next	; Next Surface


							; Set new shadow mesh to be full bright, use vertex colors, no fog.
							EntityFX ThisShadow\Mesh, 1+2+8

	
							; Multiply blend shadow mesh with stuff behind it.
							EntityBlend ThisShadow\Mesh, 2 


							; Texture the shadow mesh with the appropriate texture depending on the type of shadow it is.
							EntityTexture ThisShadow\Mesh, ThisTexture\Texture

	
						EndIf 	; If receiver is in shadow volume...

					EndIf	; If receiver <> caster...

				Next	; Next receiver.

			EndIf	; If caster is close enough to light to cast a shadow...

		Next	; Next caster.

	Next	; Next light.


	;FreeTexture Shadow_Caster_Texture
	FreeEntity Shadow_Cam
	FreeEntity Light_Pivot	


	ShowEntity Current_Camera

		
End Function


; -------------------------------------------------------------------------------------------------------------------
; This function deletes all static shadow casters which reference the specified entity.
;
; When you delete an entity which is casting a shadow, you must call this function so that the game
; does not crash from trying to reference an entity which does not exist.
;
; This function also deletes the static shadows cast by the caster specified.
;
; Idea:
; Use this function to remove the shadows cast by a building when a player destroys that building!
; -------------------------------------------------------------------------------------------------------------------
Function Delete_Static_Shadow_Caster(Entity)


	; Delete all static shadow casters which reference this entity.
	For ThisCaster.Static_Shadow_Caster = Each Static_Shadow_Caster

		If ThisCaster\Entity = Entity
			Delete ThisCaster
		EndIf

	Next
	
	
	; Delete all static shadow meshes which reference this entity.	
	For ThisShadow.Static_Shadow = Each Static_Shadow
	
		If ThisShadow\Casting_Entity = Entity
			FreeEntity ThisShadow\Mesh
			Delete ThisShadow
		EndIf
	
	Next


	; Delete all static shadow textures which reference this entity.
	For ThisTexture.Static_Shadow_Texture = Each Static_Shadow_Texture
	
		If ThisTexture\Casting_Entity = Entity
			FreeTexture ThisTexture\Texture
			Delete ThisTexture
		EndIf
	
	Next
			

End Function


; -------------------------------------------------------------------------------------------------------------------
; This function returns the X axis scale of an entity, as set by ScaleEntity().
; -------------------------------------------------------------------------------------------------------------------
;Function EntityScaleX#(Entity)
;
;	Vx# = GetMatElement(Entity, 0, 0)
;	Vy# = GetMatElement(Entity, 0, 1)
;	Vz# = GetMatElement(Entity, 0, 2)	
;	
;	Scale# = Sqr(Vx#*Vx# + Vy#*Vy# + Vz#*Vz#)
;	
;	Return Scale#
;
;End Function


; -------------------------------------------------------------------------------------------------------------------
; This function returns the Y axis scale of an entity, as set by ScaleEntity().
; -------------------------------------------------------------------------------------------------------------------
;Function EntityScaleY#(Entity)
;
;	Vx# = GetMatElement(Entity, 1, 0)
;	Vy# = GetMatElement(Entity, 1, 1)
;	Vz# = GetMatElement(Entity, 1, 2)	
;	
;	Scale# = Sqr(Vx#*Vx# + Vy#*Vy# + Vz#*Vz#)
;	
;	Return Scale#
;
;End Function


; -------------------------------------------------------------------------------------------------------------------
; This function returns the Z axis scale of an entity, as set by ScaleEntity().
; -------------------------------------------------------------------------------------------------------------------
;Function EntityScaleZ#(Entity)
;
;	Vx# = GetMatElement(Entity, 2, 0)
;	Vy# = GetMatElement(Entity, 2, 1)
;	Vz# = GetMatElement(Entity, 2, 2)	
;	
;	Scale# = Sqr(Vx#*Vx# + Vy#*Vy# + Vz#*Vz#)
;	
;	Return Scale#
;
;End Function
;~IDEal Editor Parameters:
;~C#Blitz3D