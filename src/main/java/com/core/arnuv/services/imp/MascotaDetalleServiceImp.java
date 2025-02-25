package com.core.arnuv.services.imp;

import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.core.arnuv.model.MascotaDetalle;
import com.core.arnuv.repository.IMascotaDetalleRepository;
import com.core.arnuv.service.IMascotaDetalleService;

@Service
@Component
@Slf4j
@RequiredArgsConstructor
public class MascotaDetalleServiceImp implements IMascotaDetalleService {
	private final IMascotaDetalleRepository repo;
	private final FirebaseFileService firebaseFileService;

	@Override
	public List<MascotaDetalle> listarMascotasDetalle() {
		return repo.findAll();
	}

	@Override
	public MascotaDetalle insertarMascotaDetalle(MascotaDetalle data) throws IOException {
		System.out.println(data.getPhotoPet().isEmpty());
		try {
			if (data.getIdmascota()!= null){
				MascotaDetalle existeMascota = repo.findByIdmascota(data.getIdmascota());
				if (existeMascota != null) {
					log.info("Service for create the sinister");
					String fileUrl = Strings.EMPTY;
					if (data.getPhotoPet() != null && !data.getPhotoPet().isEmpty()) {
						fileUrl = firebaseFileService.saveFile(data.getPhotoPet());
						log.info("File url: " + fileUrl);
						data.setUrlPhotoPet(fileUrl);
					}
					else {
						data.setUrlPhotoPet(existeMascota.getUrlPhotoPet());
					}
				}
			}else {
				log.info("Service for create the sinister");
				String fileUrl;
				if (data.getPhotoPet() != null && !data.getPhotoPet().isEmpty()) {
					fileUrl = firebaseFileService.saveFile(data.getPhotoPet());
					log.info("File url: " + fileUrl);
					data.setUrlPhotoPet(fileUrl);
				}
			}
		} catch (Exception e) {
			data.setUrlPhotoPet("");
		}
		
		return repo.save(data);
	}

	@Override
	public MascotaDetalle actualizarMascotaDetalle(MascotaDetalle data) {
		return repo.save(data);
	}

	@Override
	public boolean EliminarMascotaDetalle(int codigo) {
		try {
			repo.deleteById(codigo);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error al eliminar item");
			return false;
		}
	}

	@Override
	public MascotaDetalle buscarMascotaID(int codigo) {
		return repo.findById(codigo).get();
	}

	@Override
	public List<MascotaDetalle> findByIdpersonaId(int idpersona) {

		return repo.findByIdpersonaId(idpersona);
	}

	@Override
	public MascotaDetalle findByIdmascota(int idmascota) {
		return repo.findByIdmascota(idmascota);
	}

}
