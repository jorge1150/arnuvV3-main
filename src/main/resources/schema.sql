DO $$ 
DECLARE 
    v_idpersona INT;
    v_idusuario INT;
BEGIN
    -- Insertar en personadetalle si no existe y obtener el id generado
    INSERT INTO public.personadetalle (anios_experiencia, apellidos, celular, email, experiencia_previa, fechaingreso, fechamodificacion, identificacion, idusuarioing, idusuariomod, nombres, tamanos_aceptados)
    SELECT NULL, 'admin', '4353463466', 'arnuvadmin@gmail.com', NULL, '2025-02-24', NULL, '435345543', NULL, NULL, 'arnuv', '{}'
    WHERE NOT EXISTS (
        SELECT 1 FROM public.personadetalle WHERE email = 'arnuvadmin@gmail.com'
    )
    RETURNING idpersona INTO v_idpersona;

    -- Si no se insertó un nuevo registro, obtener el id existente
    IF v_idpersona IS NULL THEN
        SELECT idpersona INTO v_idpersona FROM public.personadetalle WHERE email = 'arnuvadmin@gmail.com';
    END IF;

    -- Insertar en ubicacion si no existe para la persona creada
    INSERT INTO public.ubicacion (descripcion, is_default, latitud, longitud, idpersona)
    SELECT 1, -0.247406, -78.53766, v_idpersona
    WHERE NOT EXISTS (
        SELECT 1 FROM public.ubicacion WHERE idpersona = v_idpersona
    );

    -- Insertar en usuariodetalle si no existe y obtener el id generado
    INSERT INTO public.usuariodetalle (cambiopassword, estado, fechaaprobacion, fechaingreso, fechamodificacion, idusuarioaprobacion, idusuarioing, idusuariomod, observacion, "password", username, idpersona, token_id)
    SELECT NULL, 1, NULL, '2025-02-24', NULL, NULL, NULL, NULL, '', '$2a$10$ElacdXmbOJ.hEE.ku5eJxOw1xmWnicV.KoqFs396KtkGtMlz/Ln0G', 'arnuvadmin', v_idpersona, NULL
    WHERE NOT EXISTS (
        SELECT 1 FROM public.usuariodetalle WHERE username = 'arnuvadmin'
    )
    RETURNING idusuario INTO v_idusuario;

    -- Si no se insertó un nuevo registro, obtener el id existente
    IF v_idusuario IS NULL THEN
        SELECT idusuario INTO v_idusuario FROM public.usuariodetalle WHERE username = 'arnuvadmin';
    END IF;

    -- Insertar en usuariorol si no existe para el usuario creado
    INSERT INTO public.usuariorol (fechaingreso, fechamodificacion, idususarioing, idususariomod, idrol, idusuario)
    SELECT NULL, NULL, NULL, NULL, 2, v_idusuario
    WHERE NOT EXISTS (
        SELECT 1 FROM public.usuariorol WHERE idrol = 2 AND idusuario = v_idusuario
    );
END $$;
