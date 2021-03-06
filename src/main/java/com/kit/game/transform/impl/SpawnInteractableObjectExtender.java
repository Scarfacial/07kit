package com.kit.game.transform.impl;

import com.kit.game.transform.model.MethodDefinition;
import com.kit.api.event.Events;
import com.kit.api.event.SpawnInteractableObjectEvent;
import com.kit.game.transform.Extender;
import com.kit.game.transform.model.ClassDefinition;
import com.kit.game.transform.model.MethodDefinition;
import org.apache.log4j.Logger;
import com.kit.game.transform.Extender;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class SpawnInteractableObjectExtender implements Extender {
	private static Logger logger = Logger.getLogger(MessageExtender.class);
	private MethodDefinition methodDef;

	public SpawnInteractableObjectExtender(Map<String, ClassDefinition> definitions) {
		classes:
		for (ClassDefinition def : definitions.values()) {
			for (MethodDefinition methodDefinition : def.methods) {
				if (methodDefinition.name.equals("spawnInteractableObject")) {
					methodDef = methodDefinition;
					break classes;
				}
			}
		}
		if (methodDef == null) {
			logger.error("spawnInteractableObject is null");
		}
	}

	@Override
	public boolean canRun(ClassNode clazz) {
		return methodDef != null &&
				methodDef.owner.equals(clazz.name);
	}

	@Override
	public void apply(Map<String, ClassDefinition> definitions, ClassNode clazz) {
		for (MethodNode method : (List<MethodNode>) clazz.methods) {
			if (method.name.equals(methodDef.actualName) && method.desc.equals(methodDef.actualDesc)) {
				InsnList current = method.instructions;
				InsnList inject = new InsnList();

				inject.add(new MethodInsnNode(INVOKESTATIC, "client", "getEventBus", "()L" + Events.class.getCanonicalName().replaceAll("\\.", "/") + ";"));
				inject.add(new TypeInsnNode(NEW, SpawnInteractableObjectEvent.class.getCanonicalName().replaceAll("\\.", "/")));

				List<VarInsnNode> iloads = new ArrayList<>();
				VarInsnNode boolLoad = null;

				for (int i = 0; i < methodDef.paramDescs.size(); i++) {
					if (iloads.size() == 10 && boolLoad != null) {
						continue;
					}
					if (methodDef.paramDescs.get(i).split("~")[0].equals("Z")) {
						boolLoad = new VarInsnNode(Opcodes.ILOAD, i + 1);
					} else if (methodDef.paramDescs.get(i).split("~")[0].equals("I")) {
						iloads.add(new VarInsnNode(Opcodes.ILOAD, i + 1));
					}
				}

				inject.add(new InsnNode(Opcodes.DUP));
				iloads.forEach(inject::add);
				inject.add(boolLoad);
				inject.add(new MethodInsnNode(INVOKESPECIAL, SpawnInteractableObjectEvent.class.getCanonicalName().replaceAll("\\.", "/"), "<init>", "(IIIIIIIIIIZ)V"));
				inject.add(new MethodInsnNode(INVOKEVIRTUAL, Events.class.getCanonicalName().replaceAll("\\.", "/"), "submitSpawnInteractableObjectEvent", "(L" + SpawnInteractableObjectEvent.class.getCanonicalName().replaceAll("\\.", "/") + ";)V"));
				current.insertBefore(current.getFirst(), inject);
				method.visitEnd();
			}
		}
	}
}
