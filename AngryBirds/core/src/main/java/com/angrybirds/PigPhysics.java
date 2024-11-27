package com.angrybirds;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

// PhysicsManager.java
class PigPhysics implements ContactListener {
    private World world;
    private static final float FRICTION = 1f;
    private static final float RESTITUTION = 0.5f;
    private static final float ANGULAR_DAMPING = 0.5f;
    private static final float LINEAR_DAMPING = 0.2f;

    public PigPhysics(World world) {
        this.world = world;
        world.setContactListener(this);
    }

    public Body createStructureBody(float x, float y, float width, float height, float density) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.angularDamping = ANGULAR_DAMPING;
        bodyDef.linearDamping = LINEAR_DAMPING;

        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        float radius = Math.min(width, height) / 2;
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = FRICTION;
        fixtureDef.restitution = RESTITUTION;

        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        float impactForce = calculateImpactForce(bodyA, bodyB);

        WorldManifold manifold = contact.getWorldManifold();
        if (manifold.getNumberOfContactPoints() > 0) {
            applyRotationalForce(bodyA, bodyB, manifold.getPoints()[0], impactForce);
        }
    }

    private float calculateImpactForce(Body bodyA, Body bodyB) {
        float relativeVelocityX = bodyA.getLinearVelocity().x - bodyB.getLinearVelocity().x;
        float relativeVelocityY = bodyA.getLinearVelocity().y - bodyB.getLinearVelocity().y;
        return (float) Math.sqrt(relativeVelocityX * relativeVelocityX + relativeVelocityY * relativeVelocityY);
    }

    private void applyRotationalForce(Body bodyA, Body bodyB, Vector2 contactPoint, float impactForce) {
        applyTorqueToBody(bodyA, contactPoint, impactForce);
        applyTorqueToBody(bodyB, contactPoint, -impactForce);
    }

    private void applyTorqueToBody(Body body, Vector2 contactPoint, float impactForce) {
        if (body.getType() == BodyDef.BodyType.DynamicBody) {
            Vector2 centerOfMass = body.getWorldCenter();
            Vector2 torqueArm = contactPoint.cpy().sub(centerOfMass);
            float torque = torqueArm.len() * impactForce * 0.1f;
            body.applyTorque(torque, true);
        }
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (fixtureA.getUserData() instanceof Structure && fixtureB.getUserData() instanceof Structure) {
            contact.setFriction(FRICTION * 1.2f);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
