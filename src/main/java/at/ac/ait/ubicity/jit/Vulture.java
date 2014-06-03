/**
    Copyright (C) 2014  AIT / Austrian Institute of Technology
    http://www.ait.ac.at

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see http://www.gnu.org/licenses/agpl-3.0.html
 */
package at.ac.ait.ubicity.jit;


/**
 *
 * @author jan van oort
 */
final class Vulture extends Thread {

	protected final JitIndexingPlugin jitController;

	public Vulture(final JitIndexingPlugin _jitController) {
		super(_jitController.threadGroup, "Connection Vulture");
		jitController = _jitController;
		this.start();
	}

	@Override
	public synchronized void run() {
		while (true) {
			try {
				this.wait(1000);
			} catch (InterruptedException _interrupt) {
				Thread.interrupted();
			}
			synchronized (jitController.connections) {
				for (int i = 0; i < jitController.connections.size(); i++) {
					Connection _c = jitController.connections.elementAt(i);
					if (!(_c.isAlive())) {
						jitController.connections.removeElementAt(i);

						i--;
					}
				}
			}
		}
	}
}
