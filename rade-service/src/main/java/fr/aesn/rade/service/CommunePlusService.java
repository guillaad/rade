/*  This file is part of the Rade project (https://github.com/mgimpel/rade).
 *  Copyright (C) 2018 Marc Gimpel
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
/* $Id$ */
package fr.aesn.rade.service;

import java.util.Date;
import java.util.List;

import fr.aesn.rade.common.modelplus.CommunePlus;
import fr.aesn.rade.common.modelplus.CommunePlusWithGenealogie;

/**
 * Service Interface for CommunePlus.
 * @author Marc Gimpel (mgimpel@gmail.com)
 */
public interface CommunePlusService {
  /**
   * List all Commune valid at the given date.
   * @param date the date at which the code was valid
   * @return a List of all the Commune.
   */
  public List<CommunePlus> getAllCommune(Date date);

  /**
   * Get the Commune with the given code at the given date.
   * @param code the Commune code.
   * @param date the date at which the code was valid
   * @return the Commune with the given code at the given date.
   */
  public CommunePlus getCommuneByCode(String code, Date date);

  /**
   * Get the Commune with the given code at the given date.
   * @param code the Commune code.
   * @param date the date at which the code was valid
   * @return the Commune with the given code at the given date.
   */
  public CommunePlus getCommuneByCode(String code, String date);
  
  /**
   * Returns a List of all Commune from the given codeInsee, departement, 
   * region, circonscription, commune name and/or date.
   * @param codeInsee the code of the Communes.
   * @param codeDept the departement of the Communes.
   * @param codeBassin the circonscription of the Communes
   * @param codeRegion the region of the Communes.
   * @param nomCommune a pattern to search for Communes with a name resembling.
   * @param dateEffet the date at which the Communes were valid.
   * @return a List of all Commune matching the given parameters.
   */
  public List<CommunePlus> getCommuneByCriteria(String codeInsee,
                                            String codeDept,
                                            String codeBassin,
                                            String codeRegion,
                                            String nomCommune,
                                            Date dateEffet);
                                            
  /**
   * Get the Commune with the given code at the given date, and all it's
   * genealogie.
   * @param code the Commune code.
   * @param date the date at which the code was valid
   * @return the Commune with the given code at the given date, and all it's
   * genealogie.
   */
  public CommunePlusWithGenealogie getCommuneWithGenealogie(final String code,
  
}
