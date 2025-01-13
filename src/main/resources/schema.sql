INSERT INTO public.personadetalle(apellidos, celular, email,  identificacion,  nombres)
VALUES('admin', '0967325098', 'admin@gmail.com', '0105022248', 'admin');

INSERT INTO public.usuariodetalle ("password", username, idpersona)
VALUES('$2a$10$24gd1zfug.iQ5Z7Tv6tSduXt9iZCc5V2LFgJn0jbFriseX0dpnpiS', 'admin', 1);

INSERT INTO public.rol (nombre) VALUES('PASEADOR');
